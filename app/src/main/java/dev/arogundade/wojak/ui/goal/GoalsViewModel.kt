package dev.arogundade.wojak.ui.goal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Portfolio
import dev.arogundade.wojak.models.PortfolioCurrencyTransactions
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.repository.PortfolioRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoalsViewModel
@Inject
constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _goals =
        MutableLiveData<Result<List<PortfolioCurrencyTransactions>>>(Result.Loading())
    val portfolioCurrencyTransactions: LiveData<Result<List<PortfolioCurrencyTransactions>>> =
        _goals

    fun getCurrencies() {
        viewModelScope.launch {
            portfolioRepository.allPortfolioCurrencyTransactionsWithGoal()
                .collect { portfolioCurrencyTransactions ->
                    _goals.postValue(Result.Success(portfolioCurrencyTransactions))
                }
        }
    }

    fun updateGoal(portfolio: Portfolio, amount: Double?) {
        viewModelScope.launch {
            portfolioRepository.updatePortfolio(
                portfolio.copy(
                    goalAmount = amount ?: portfolio.goalAmount
                )
            )
        }
    }

    fun deleteGoal(portfolio: Portfolio) {
        viewModelScope.launch {
            portfolioRepository.updatePortfolio(
                portfolio.copy(
                    goalAmount = null,
                    goalTitle = null
                )
            )
        }
    }

}