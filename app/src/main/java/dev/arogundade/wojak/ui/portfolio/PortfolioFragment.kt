package dev.arogundade.wojak.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.adapters.PortfolioAdapter
import dev.arogundade.wojak.databinding.FragmentPortfolioBinding
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.pager.PagerFragment
import dev.arogundade.wojak.ui.EmptyItem
import dev.arogundade.wojak.ui.home.HomeFragmentDirections
import dev.arogundade.wojak.ui.choose.ChooseCoinFragment
import dev.arogundade.wojak.ui.dialogs.Filter
import dev.arogundade.wojak.utils.Extensions.count
import dev.arogundade.wojak.utils.Extensions.holdings
import dev.arogundade.wojak.utils.Extensions.isFiltering
import javax.inject.Inject

@AndroidEntryPoint
class PortfolioFragment : PagerFragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private val viewModel: PortfolioViewModel by viewModels()

    private lateinit var binding: FragmentPortfolioBinding
    private lateinit var portfolioAdapter: PortfolioAdapter
    private lateinit var emptyItem: EmptyItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPortfolioBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrencies()
    }

    override fun onClick() {
        ChooseCoinFragment { currencies ->
            if (currencies.isNotEmpty()) {
                val destination =
                    HomeFragmentDirections.actionHomeFragmentToAddToPortfolioFragment(
                        currencies.first()
                    )
                findNavController().navigate(destination)
            }
        }.show(childFragmentManager, "childFragmentManager")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyItem = EmptyItem(binding.empty) {
            ChooseCoinFragment { currencies ->
                if (currencies.isNotEmpty()) {
                    val destination =
                        HomeFragmentDirections.actionHomeFragmentToAddToPortfolioFragment(
                            currencies.first()
                        )
                    findNavController().navigate(destination)
                }
            }.show(childFragmentManager, "childFragmentManager")
        }.apply {
            setText(
                "Add a Currency",
                "You do not have a currency in your portfolio. Tap on 'Add a Currency' to get started."
            )
        }

        portfolioAdapter = PortfolioAdapter(requestManager) { portfolioCurrencyTransactions ->
            val direction =
                HomeFragmentDirections.actionHomeFragmentToCoinInfoFragment(
                    portfolioCurrencyTransactions
                )
            findNavController().navigate(direction)
        }

        binding.apply {
            filter.setOnClickListener {
                Filter(viewModel.filterResult) { filterResult ->
                    viewModel.filterResult = filterResult
                    if (filterResult.count() > 0) {
                        filterCount.apply {
                            text = filterResult.count().toString()
                            visibility = View.VISIBLE
                        }
                    } else {
                        filterCount.visibility = View.GONE
                    }
                    viewModel.getCurrencies()
                }.show(childFragmentManager, "childFragmentManager")
            }

            currencies.apply {
                adapter = portfolioAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        viewModel.portfolioCurrencyTransactions.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data?.sortedBy {
                        it.transactions.holdings() * (it.currency.price?.toDouble() ?: 0.0)
                    }?.reversed() ?: emptyList()
                    if (data.isEmpty() && !viewModel.filterResult.isFiltering()) {
                        emptyItem.show()
                    } else {
                        emptyItem.hide()
                    }
                    portfolioAdapter.setCurrencies(data)
                }
            }
        }

    }

}