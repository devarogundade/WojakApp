package dev.arogundade.wojak.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.arogundade.wojak.R
import dev.arogundade.wojak.databinding.FragmentHomeBinding
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.pager.PagerAdapter
import dev.arogundade.wojak.storage.WojakDatastore
import dev.arogundade.wojak.ui.alert.AlertFragment
import dev.arogundade.wojak.ui.converter.ConverterFragment
import dev.arogundade.wojak.ui.goal.GoalsFragment
import dev.arogundade.wojak.ui.portfolio.PortfolioFragment
import dev.arogundade.wojak.utils.Extensions.smartRound
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val portfolioFragment = PortfolioFragment()
    private val converterFragment = ConverterFragment()
    private val goalsFragment = GoalsFragment()
    private val alertFragment = AlertFragment()

    @Inject
    lateinit var datastore: WojakDatastore

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTransaction()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pagerAdapter = PagerAdapter(
            requireActivity(), listOf(
                portfolioFragment,
                converterFragment,
                goalsFragment,
                alertFragment
            )
        )

        binding.apply {
            viewPager.apply {
                this.adapter = pagerAdapter
            }

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> getString(R.string.portfolio)
                    1 -> getString(R.string.convert)
                    2 -> getString(R.string.goals)
                    else -> getString(R.string.alerts)
                }
            }.attach()

            addToPortfolio.setOnClickListener {
                pagerAdapter.onClick(viewPager.currentItem)
            }
        }

        lifecycleScope.launch {
            datastore.setFirstTime()
        }

        viewModel.price.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {

                }
                is Result.Error -> Unit
                is Result.Success -> {
                    binding.price.text = "$${smartRound(result.data?.get(0) ?: 0.0)}"

                    val mPrice = result.data?.get(0) ?: 0.0
                    val pPrice = result.data?.get(1) ?: 0.0
                    val percentage = ((mPrice - pPrice) / mPrice) * 100

                    binding.percentage.text = "${smartRound(percentage)}% all time"
                }
            }
        }

    }

}