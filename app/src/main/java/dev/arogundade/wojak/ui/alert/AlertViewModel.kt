package dev.arogundade.wojak.ui.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.CurrencyAlerts
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.repository.AlertRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    private val _alerts = MutableLiveData<Result<List<CurrencyAlerts>>>(Result.Loading())
    val alerts: LiveData<Result<List<CurrencyAlerts>>> = _alerts

    fun getAlerts() {
        viewModelScope.launch {
            alertRepository.getCurrencyAlerts().collect { currencyAlerts ->
                val alerts = currencyAlerts.filter { it.alerts.isNotEmpty() }
                _alerts.postValue(Result.Success(alerts))
            }
        }
    }

    fun enableAlert(alert: Alert, currency: Currency) {
        viewModelScope.launch {
            alertRepository.enableAlert(alert, currency)
        }
    }

    fun disableAlert(alert: Alert) {
        viewModelScope.launch {
            alertRepository.disAbleAlert(alert)
        }
    }

    fun deleteAlert(alert: Alert) {
        viewModelScope.launch {
            alertRepository.deleteAlert(alert)
        }
    }

}