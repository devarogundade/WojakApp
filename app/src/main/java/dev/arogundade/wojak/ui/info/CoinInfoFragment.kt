package dev.arogundade.wojak.ui.info

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.adapters.TransactionAdapter
import dev.arogundade.wojak.databinding.FragmentCoinInfoBinding
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.repository.TransactionRepository
import dev.arogundade.wojak.ui.dialogs.TransactionInfo
import dev.arogundade.wojak.utils.WojakItemDecoration
import dev.arogundade.wojak.utils.Extensions
import dev.arogundade.wojak.utils.Extensions.holdings
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.Extensions.spent
import dev.arogundade.wojak.utils.Extensions.toChartDataCollection
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@AndroidEntryPoint
class CoinInfoFragment : Fragment() {

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var prettyTime: PrettyTime

    private lateinit var portfolioCurrencyTransactions: PortfolioCurrencyTransactions
    private lateinit var binding: FragmentCoinInfoBinding
    private lateinit var transactionAdapter: TransactionAdapter
    private val viewModel: CoinInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            portfolioCurrencyTransactions =
                it.getSerializable("data") as PortfolioCurrencyTransactions
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTransactions(portfolioCurrencyTransactions.currency)
        viewModel.loadChartData(
            portfolioCurrencyTransactions.currency,
            TransactionRepository.INTERVAL.DAY
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCoinInfoBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        transactionAdapter = TransactionAdapter(prettyTime) { transaction ->
            TransactionInfo(transaction, { editTransaction(it) }, { deleteTransaction(it) }).show(
                childFragmentManager,
                "childFragmentManager"
            )
        }

        binding.apply {
            name.text = portfolioCurrencyTransactions.currency.name

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab) {
                        tabLayout.getTabAt(0) -> viewModel.loadChartData(
                            portfolioCurrencyTransactions.currency,
                            TransactionRepository.INTERVAL.DAY
                        )
                        tabLayout.getTabAt(1) -> viewModel.loadChartData(
                            portfolioCurrencyTransactions.currency,
                            TransactionRepository.INTERVAL.WEEK
                        )
                        tabLayout.getTabAt(2) -> viewModel.loadChartData(
                            portfolioCurrencyTransactions.currency,
                            TransactionRepository.INTERVAL.MONTH
                        )
                        tabLayout.getTabAt(3) -> viewModel.loadChartData(
                            portfolioCurrencyTransactions.currency,
                            TransactionRepository.INTERVAL.YEAR
                        )
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

                override fun onTabReselected(tab: TabLayout.Tab?) = Unit
            })

            if (portfolioCurrencyTransactions.currency.logo_url != null && portfolioCurrencyTransactions.currency.logo_url!!.isSvg()) {
                val uri = Uri.parse(portfolioCurrencyTransactions.currency.logo_url)
                requestManager
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                    .load(uri)
                    .into(image)
                requestManager
                    .`as`(PictureDrawable::class.java)
                    .listener(SvgSoftwareLayerSetter())
                    .load(uri)
                    .into(cover)
            } else {
                requestManager
                    .load(portfolioCurrencyTransactions.currency.logo_url)
                    .into(image)
                requestManager
                    .load(portfolioCurrencyTransactions.currency.logo_url)
                    .into(cover)
            }

            transactions.apply {
                adapter = transactionAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    WojakItemDecoration(
                        color = ContextCompat.getColor(
                            requireContext(),
                            R.color.md_theme_light_outline
                        ),
                        height = 1
                    )
                )
            }

            addToPortfolio.setOnClickListener {
                val destination =
                    CoinInfoFragmentDirections.actionCoinInfoFragmentToNewTransactionFragment(
                        portfolioCurrencyTransactions.currency
                    )
                findNavController().navigate(destination)
            }

            addAlert.setOnClickListener {
                val destination =
                    CoinInfoFragmentDirections.actionCoinInfoFragmentToNewAlertFragment(
                        portfolioCurrencyTransactions.currency
                    )
                findNavController().navigate(destination)
            }
        }

        viewModel.chartData.observe(viewLifecycleOwner) { transactions ->
            binding.chartView.apply {
                setData(transactions.toChartDataCollection())
                applyTouch { range, view -> view.openLabelFor(range) }
            }
        }

        val marketPrice = portfolioCurrencyTransactions.currency.price?.toDouble() ?: 0.0
        binding.marketPrice.text = "$${Extensions.smartRound(marketPrice)}"

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.setTransactions(transactions)

            val spent = transactions.spent()
            val holdings = transactions.holdings()

            val avgSpent = spent / holdings

            binding.apply {
                this.holdings.text =
                    "${Extensions.smartRound(holdings)} ${portfolioCurrencyTransactions.currency.symbol}"
                this.price.text =
                    "$${
                        Extensions.smartRound(
                            portfolioCurrencyTransactions.currency.price.toString()
                                .toDouble() * holdings
                        )
                    }"

                this.spent.text = "$${Extensions.smartRound(spent)}"

                when {
                    avgSpent < marketPrice -> {
                        this.avgPrice.apply {
                            text = "$${Extensions.smartRound(avgSpent)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_pump
                                )
                            )
                        }
                        this.marketPrice.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_error
                                )
                            )
                        }
                    }
                    avgSpent > marketPrice -> {
                        this.avgPrice.apply {
                            text = "$${Extensions.smartRound(avgSpent, true)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_error
                                )
                            )
                        }
                        this.marketPrice.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_pump
                                )
                            )
                        }
                    }
                    else -> {
                        this.avgPrice.apply {
                            text = "$${Extensions.smartRound(avgSpent)}"
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_onSurfaceVariant
                                )
                            )
                        }
                        this.marketPrice.apply {
                            setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.md_theme_light_onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }

        }
    }

    private fun deleteTransaction(transaction: Transaction) {
        viewModel.deleteTransaction(transaction)
        Snackbar.make(binding.root, "Made a mistake?", Snackbar.LENGTH_SHORT).apply {
            this.setAction("Undo delete") {
                viewModel.addTransaction(transaction)
            }
        }.show()
    }

    private fun editTransaction(transaction: Transaction) {
        val destination = CoinInfoFragmentDirections.actionCoinInfoFragmentToNewTransactionFragment(
            portfolioCurrencyTransactions.currency,
            transaction
        )
        findNavController().navigate(destination)
    }

}