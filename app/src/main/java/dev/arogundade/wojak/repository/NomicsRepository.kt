package dev.arogundade.wojak.repository

import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.networking.clients.NomicsClient
import dev.arogundade.wojak.storage.WojakDatabase
import dev.arogundade.wojak.utils.Constants.NETWORK_PAGE_SIZE
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class NomicsRepository
@Inject
constructor(
    private val nomicsClient: NomicsClient,
    private val database: WojakDatabase
) {
    private fun refreshCurrencies(
        page: Int,
        ids: String,
        perPage: Int,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = nomicsClient.currencies(page = page, ids = ids, perPage = perPage)

                val data = request.body()
                if (request.isSuccessful && data != null) {
                    data.forEach { currency ->
                        database.currencyDao().save(currency)
                    }
                }
            } catch (e: Exception) {
                cancel(e.message ?: "")
            }
        }
    }

    fun getCurrencies(
        page: Int = 1,
        ids: String = "",
        perPage: Int = NETWORK_PAGE_SIZE
    ): Flow<List<Currency>> {
        refreshCurrencies(page, ids, perPage)
        return database.currencyDao().allCurrencies()
    }
}