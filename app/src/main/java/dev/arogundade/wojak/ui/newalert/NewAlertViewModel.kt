package dev.arogundade.wojak.ui.newalert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.repository.AlertRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAlertViewModel
@Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {

    fun addAlert(alert: Alert) {
        viewModelScope.launch {
            alertRepository.addAlert(alert)
        }
    }

}