package dev.arogundade.wojak.ui.converter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.repository.CurrencyRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel
@Inject
constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _currency = MutableLiveData<Map<Currency?, Currency?>>()
    val currency: LiveData<Map<Currency?, Currency?>> = _currency

    fun getCurrencies(from: String, to: String) {
        viewModelScope.launch {
            val flow1 = currencyRepository.getCurrency(from).distinctUntilChangedBy { it?.price }
            val flow2 = currencyRepository.getCurrency(to).distinctUntilChangedBy { it?.price }

            flow1.combine(flow2) { from, to -> _currency.postValue(mapOf(from to to)) }.collect()
        }
    }

}