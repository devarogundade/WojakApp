package dev.arogundade.wojak.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.arogundade.wojak.models.Portfolio
import dev.arogundade.wojak.repository.PortfolioRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddToPortfolioViewModel
@Inject
constructor(
    private val portfolioRepository: PortfolioRepository
) : ViewModel() {

    fun addToPortFolio(portfolio: Portfolio) {
        viewModelScope.launch {
            portfolioRepository.addToPortfolio(portfolio)
        }
    }

}