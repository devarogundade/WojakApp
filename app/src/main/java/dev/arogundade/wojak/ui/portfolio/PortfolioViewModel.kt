package dev.arogundade.wojak.ui.portfolio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.FilterResult
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.repository.PortfolioRepository
import dev.arogundade.wojak.utils.Extensions.holdings
import dev.arogundade.wojak.utils.Extensions.isAll
import dev.arogundade.wojak.utils.Extensions.isProfitOnly
import dev.arogundade.wojak.utils.Extensions.spent
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel
@Inject
constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    var filterResult = FilterResult()

    private val _portfolioCurrencyTransactions =
        MutableLiveData<Result<List<PortfolioCurrencyTransactions>>>(Result.Loading())
    val portfolioCurrencyTransactions: LiveData<Result<List<PortfolioCurrencyTransactions>>> =
        _portfolioCurrencyTransactions

    fun getCurrencies() {
        viewModelScope.launch {
            portfolioRepository.allPortfolioCurrencyTransactions()
                .collect { portfolioCurrencyTransactions ->
                    val data = portfolioCurrencyTransactions.filter {
                        if (filterResult.hasGoal) it.portfolio.goalTitle != null
                        else true
                    }.filter {
                        if (filterResult.isAll()) true
                        else {
                            val spent = it.transactions.spent()
                            val holdings = it.transactions.holdings()
                            val avgSpent = spent / holdings
                            val marketPrice = it.currency.price?.toDouble() ?: 0.0

                            if (filterResult.isProfitOnly()) {
                                avgSpent < marketPrice
                            } else {
                                avgSpent > marketPrice
                            }
                        }
                    }

                    _portfolioCurrencyTransactions.postValue(
                        Result.Success(data)
                    )
                }
        }
    }

}