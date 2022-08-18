package dev.arogundade.wojak.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.Transaction
import dev.arogundade.wojak.repository.TransactionRepository
import dev.arogundade.wojak.utils.Extensions.holdings
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel
@Inject
constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    var holdings = 0.0

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.addTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

    fun getHoldings(currency: Currency) {
        viewModelScope.launch {
            transactionRepository.allTransaction(currency.id).collect { transactions ->
                holdings = transactions.holdings()
            }
        }
    }

}