package dev.arogundade.wojak.ui.choose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.adapters.ChooseCurrencyAdapter
import dev.arogundade.wojak.databinding.FragmentChooseCoinBinding
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.networking.observer.NetworkConnectivityObserver
import dev.arogundade.wojak.ui.ConnectivityStatus
import dev.arogundade.wojak.utils.Extensions
import dev.arogundade.wojak.utils.WojakItemDecoration
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChooseCoinFragment(
    private val shouldDismiss: Boolean = true,
    private val onSelect: (List<Currency>) -> Unit
) : BottomSheetDialogFragment() {

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    private val viewModel: ChooseCoinViewModel by viewModels()
    private lateinit var binding: FragmentChooseCoinBinding
    private lateinit var chooseCurrencyAdapter: ChooseCurrencyAdapter
    private var connectivityStatus: ConnectivityStatus? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getCurrencies()
        lifecycleScope.launch {
            networkConnectivityObserver.observe()
                .distinctUntilChanged()
                .collect { status ->
                    connectivityStatus?.setStatus(status)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseCoinBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chooseCurrencyAdapter = ChooseCurrencyAdapter(
            requestManager,
            object : ChooseCurrencyAdapter.OnChooseListener {
                override fun onClick(currency: Currency) {
                    onCurrencyClick(currency)
                }

                override fun onHold(currency: Currency) {}
            }
        )

        if (dialog != null) {
            Extensions.expand(view, dialog!!, requireActivity())
            dialog!!.window?.setDimAmount(0f)
        }

        binding.apply {
            this@ChooseCoinFragment.connectivityStatus =
                ConnectivityStatus(binding.connectivityStatus)

            currencies.apply {
                adapter = chooseCurrencyAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    WojakItemDecoration(
                        color = ContextCompat.getColor(
                            requireContext(),
                            R.color.md_theme_light_outline
                        ),
                        height = 1,
                        firstLine = true
                    )
                )
            }
        }

        lifecycleScope.launchWhenResumed {
            viewModel.currencies.observe(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Error -> {

                        binding.progressBar.visibility = View.GONE
                    }
                    is Result.Success -> {
                        val data = resource.data ?: emptyList()
                        chooseCurrencyAdapter.setCurrencies(data)
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun onCurrencyClick(currency: Currency) {
        onSelect(listOf(currency))
        if (shouldDismiss) {
            dismiss()
        }
    }
}