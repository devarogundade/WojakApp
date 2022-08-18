package dev.arogundade.wojak.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.repository.PortfolioRepository
import dev.arogundade.wojak.utils.Extensions.holdings
import dev.arogundade.wojak.utils.Extensions.spent
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    private val _price = MutableLiveData<Result<List<Double>>>(Result.Loading())
    val price: LiveData<Result<List<Double>>> = _price

    fun getTransaction() {
        viewModelScope.launch {
            portfolioRepository.allPortfolioCurrencyTransactions()
                .collect { portfolioCurrencyTransactions ->
                    var totalMarketPrice = 0.0
                    var totalPortfolioPrice = 0.0
                    portfolioCurrencyTransactions.groupBy { it.currency.id }.forEach { map ->
                        map.value.forEach { data ->
                            val holdings = data.transactions.holdings()
                            val price = data.currency.price?.toDouble() ?: 0.0
                            totalMarketPrice += (holdings * price)
                            totalPortfolioPrice += data.transactions.spent()
                        }
                    }
                    _price.postValue(Result.Success(listOf(totalMarketPrice, totalPortfolioPrice)))
                }
        }
    }

}