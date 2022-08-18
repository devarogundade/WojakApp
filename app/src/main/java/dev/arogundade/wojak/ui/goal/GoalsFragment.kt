package dev.arogundade.wojak.ui.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.adapters.GoalAdapter
import dev.arogundade.wojak.databinding.FragmentGoalsBinding
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.pager.PagerFragment
import dev.arogundade.wojak.ui.EmptyItem
import dev.arogundade.wojak.ui.home.HomeFragmentDirections
import dev.arogundade.wojak.ui.choose.ChooseCoinFragment
import dev.arogundade.wojak.ui.dialogs.EditAmount
import dev.arogundade.wojak.utils.WojakItemDecoration

@AndroidEntryPoint
class GoalsFragment : PagerFragment() {

    private lateinit var binding: FragmentGoalsBinding
    private val viewModel: GoalsViewModel by viewModels()
    private lateinit var emptyItem: EmptyItem

    private val goalsAdapter = GoalAdapter { portfolioCurrencyTransactions ->
        EditAmount({ portfolio, amount ->
            viewModel.updateGoal(portfolio, amount)
        }, { portfolio ->
            viewModel.deleteGoal(portfolio)

            Snackbar.make(binding.root, "Made a mistake?", Snackbar.LENGTH_SHORT).apply {
                this.setAction("Undo delete") {
                    viewModel.updateGoal(portfolio, null)
                }
            }.show()

        }, portfolioCurrencyTransactions.portfolio).show(
            childFragmentManager,
            "childFragmentManager"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoalsBinding.inflate(inflater)
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
                "Add a Goal",
                "You do not have a goal in your portfolio. Tap on 'Add a Goal' to get started."
            )
        }

        viewModel.portfolioCurrencyTransactions.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> Unit
                is Result.Success -> {
                    val data = result.data ?: emptyList()
                    goalsAdapter.setGoals(data)
                    if (data.isEmpty()) {
                        emptyItem.show()
                    } else {
                        emptyItem.hide()
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.apply {
            goals.apply {
                adapter = goalsAdapter
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
        }
    }


}