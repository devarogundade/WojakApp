package dev.arogundade.wojak.ui.info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.repository.TransactionRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoinInfoViewModel
@Inject
constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>(emptyList())
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _chartData = MutableLiveData<List<List<Transaction>>>(emptyList())
    val chartData: LiveData<List<List<Transaction>>> = _chartData

    init {
        viewModelScope.launch {
            transactionRepository.filteredTransactions.collect { transactions ->
                _chartData.postValue(transactions)
            }
        }
    }

    fun getTransactions(currency: Currency) {
        viewModelScope.launch {
            viewModelScope.launch {
                transactionRepository.allTransaction(currency.id).collect { transactions ->
                    _transactions.postValue(transactions)
                }
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transaction)
        }
    }

    fun loadChartData(currency: Currency, interval: Int) {
        viewModelScope.launch {
            transactionRepository.filterTransaction(
                interval,
                currency.id
            ) {
            }
        }
    }

}