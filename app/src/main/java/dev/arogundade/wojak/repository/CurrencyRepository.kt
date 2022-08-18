package dev.arogundade.wojak.repository

import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.storage.WojakDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyRepository
@Inject
constructor(
    private val database: WojakDatabase
) {

    fun allCurrencies(): Flow<List<Currency>> {
        return database.currencyDao().allCurrencies()
    }

    fun getCurrency(currencyId: String): Flow<Currency?> {
        return database.currencyDao().getCurrency(currencyId)
    }

    suspend fun deleteCurrency(currency: Currency): Boolean {
        return try {
            database.currencyDao().delete(currency)
            true
        } catch (e: Exception) {
            false
        }
    }

}