package dev.arogundade.wojak.ui.converter

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.databinding.FragmentConverterBinding
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.pager.PagerFragment
import dev.arogundade.wojak.ui.home.HomeFragmentDirections
import dev.arogundade.wojak.ui.choose.ChooseCoinFragment
import dev.arogundade.wojak.utils.Extensions.isSvg
import dev.arogundade.wojak.utils.Extensions.smartRound
import dev.arogundade.wojak.utils.SvgSoftwareLayerSetter
import javax.inject.Inject

@AndroidEntryPoint
class ConverterFragment : PagerFragment() {

    @Inject
    lateinit var requestManager: RequestManager

    private var from: Currency? = null
    private var to: Currency? = null

    private val viewModel: ConverterViewModel by viewModels()
    private lateinit var binding: FragmentConverterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConverterBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (from == null || to == null) {
            viewModel.getCurrencies("BTC", "USDT")
        }
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

        binding.apply {
            choose.setOnClickListener {
                ChooseCoinFragment { currencies ->
                    if (currencies.isNotEmpty()) {
                        viewModel.getCurrencies(currencies.first().id, to?.id ?: "USDT")
                    }
                }.show(childFragmentManager, "childFragmentManager")
            }

            choose2.setOnClickListener {
                ChooseCoinFragment { currencies ->
                    if (currencies.isNotEmpty()) {
                        viewModel.getCurrencies(from?.id ?: "USDT", currencies.first().id)
                    }
                }.show(childFragmentManager, "childFragmentManager")
            }

            amount.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    calculate()
                }

                override fun afterTextChanged(p0: Editable?) = Unit
            })

            swap.setOnClickListener {
                swap()
            }
        }

        viewModel.currency.observe(viewLifecycleOwner) { map ->
            map.forEach { (currency, currency2) ->
                if (currency != null) {
                    from = currency

                    binding.name.text = currency.name
                    if (currency.logo_url != null && currency.logo_url.isSvg()) {
                        val uri = Uri.parse(currency.logo_url)
                        requestManager
                            .`as`(PictureDrawable::class.java)
                            .listener(SvgSoftwareLayerSetter())
                            .load(uri)
                            .into(binding.image)
                    } else {
                        requestManager
                            .load(currency.logo_url)
                            .into(binding.image)
                    }
                }

                if (currency2 != null) {
                    to = currency2

                    binding.name2.text = currency2.name
                    if (currency2.logo_url != null && currency2.logo_url.isSvg()) {
                        val uri = Uri.parse(currency2.logo_url)
                        requestManager
                            .`as`(PictureDrawable::class.java)
                            .listener(SvgSoftwareLayerSetter())
                            .load(uri)
                            .into(binding.image2)
                    } else {
                        requestManager
                            .load(currency2.logo_url)
                            .into(binding.image2)
                    }
                }

                calculate()
            }
        }

    }

    private fun swap() {
        if (from == null || to == null) return
        viewModel.getCurrencies(to!!.id, from!!.id)
    }

    private fun calculate() {
        val input = binding.amount.text.toString()

        if (input.isNotEmpty()) {
            val amount = input.toDouble()

            val worth = ((from?.price?.toDouble() ?: 0.0) * amount)
            val result = worth / ((to?.price?.toDouble() ?: 0.0))

            binding.amount2.text = smartRound(result, true)
        } else {
            binding.amount2.text = ""
        }
    }

}