package dev.arogundade.wojak.ui.alert

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.adapters.AlertAdapter
import dev.arogundade.wojak.databinding.FragmentAlertBinding
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.pager.PagerFragment
import dev.arogundade.wojak.ui.EmptyItem
import dev.arogundade.wojak.ui.choose.ChooseCoinFragment
import dev.arogundade.wojak.ui.dialogs.ConfirmationDelete
import dev.arogundade.wojak.ui.home.HomeFragmentDirections
import org.ocpsoft.prettytime.PrettyTime
import javax.inject.Inject

@AndroidEntryPoint
class AlertFragment : PagerFragment() {

    @Inject
    lateinit var prettyTime: PrettyTime

    @Inject
    lateinit var requestManager: RequestManager

    private lateinit var alertAdapter: AlertAdapter
    private lateinit var binding: FragmentAlertBinding

    private val viewModel: AlertViewModel by viewModels()
    private lateinit var emptyItem: EmptyItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlertBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAlerts()
    }

    override fun onClick() {
        createAnAlert()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyItem = EmptyItem(binding.empty) {
            createAnAlert()
        }.apply {
            setText(
                "Add an Alert",
                "You do not have an alert in your portfolio. Tap on 'Add a Alert' to get started."
            )
        }

        alertAdapter =
            AlertAdapter(prettyTime, requestManager, { alert, currency, isChecked ->
                if (isChecked) {
                    viewModel.enableAlert(alert, currency)
                    return@AlertAdapter
                }

                viewModel.deleteAlert(alert)
            }, { alert ->
                ConfirmationDelete({
                    viewModel.deleteAlert(alert)
                }, {}).show(childFragmentManager, "childFragmentManager")
            })

        binding.apply {
            alerts.apply {
                adapter = alertAdapter
                layoutManager = LinearLayoutManager(requireContext())
            }
        }

        viewModel.alerts.observe((viewLifecycleOwner)) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> Unit
                is Result.Success -> {
                    val data = result.data ?: emptyList()
                    alertAdapter.setCurrencyAlerts(data)
                    if (data.isEmpty()) {
                        emptyItem.show()
                    } else {
                        emptyItem.hide()
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun createAnAlert() {
        ChooseCoinFragment { currencies ->
            if (currencies.isNotEmpty()) {
                val destination =
                    HomeFragmentDirections.actionHomeFragmentToNewAlertFragment(
                        currencies.first()
                    )
                findNavController().navigate(destination)
            }
        }.show(childFragmentManager, "childFragmentManager")
    }
}