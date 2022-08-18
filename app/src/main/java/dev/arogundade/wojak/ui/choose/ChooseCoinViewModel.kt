package dev.arogundade.wojak.ui.choose

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.networking.Result
import dev.arogundade.wojak.repository.NomicsRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseCoinViewModel
@Inject constructor(
    private val nomicsRepository: NomicsRepository
) : ViewModel() {

    private val _currencies = MutableLiveData<Result<List<Currency>>>(Result.Loading())
    val currencies: LiveData<Result<List<Currency>>> = _currencies

    fun getCurrencies() {
        viewModelScope.launch {
            nomicsRepository.getCurrencies(1, "").distinctUntilChanged()
                .collect { currencies ->
                    _currencies.postValue(Result.Success(currencies))
                }
        }
    }

}