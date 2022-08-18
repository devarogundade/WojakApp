package dev.arogundade.wojak.repository

import android.util.Log
import dev.arogundade.wojak.models.Alert
import dev.arogundade.wojak.models.AlertCurrency
import dev.arogundade.wojak.models.Currency
import dev.arogundade.wojak.models.CurrencyAlerts
import dev.arogundade.wojak.networking.clients.NomicsClient
import dev.arogundade.wojak.storage.WojakDatabase
import dev.arogundade.wojak.utils.Extensions.ids
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlertRepository
@Inject constructor(
    private val database: WojakDatabase,
    private val nomicsClient: NomicsClient,
) {

    fun getCurrencyAlerts(): Flow<List<CurrencyAlerts>> {
        return database.currencyDao().allCurrencyAlerts()
    }

    @Throws
    suspend fun getCurrencies(): Flow<List<AlertCurrency>> {
        refreshAlertCurrenciesPrice()
        return database.alertDao().allAlertAlertCurrencies()
    }

    suspend fun disAbleAlert(alert: Alert) {
        try {
            database.alertDao().save(alert.copy(active = false))
        } catch (e: Exception) {
        }
    }

    suspend fun enableAlert(alert: Alert, currency: Currency) {
        try {
            database.alertDao().update(
                alert.copy(
                    active = true,
                    watchType = if (alert.target >= (currency.price?.toDouble() ?: 0.0)) 1 else -1,
                )
            )
        } catch (e: Exception) {
        }
    }

    suspend fun addAlert(alert: Alert) {
        try {
            database.alertDao().save(alert)
        } catch (e: Exception) {
        }
    }

    suspend fun deleteAlert(alert: Alert) {
        try {
            database.alertDao().delete(alert)
        } catch (e: Exception) {
        }
    }

    @Throws
    private suspend fun refreshAlertCurrenciesPrice() {
        val alerts = database.alertDao().allAlerts()
        Log.d("Alert", "refreshAlertCurrenciesPrice: $alerts")
        val request = nomicsClient.currencies(page = 1, ids = alerts.ids(), perPage = alerts.size)
        val data = request.body()
        Log.d("Alert", "refreshAlertCurrenciesPrice: $data")
        if (request.isSuccessful && data != null) {
            data.forEach { currency ->
                database.currencyDao().save(currency)
            }
        }
    }

}