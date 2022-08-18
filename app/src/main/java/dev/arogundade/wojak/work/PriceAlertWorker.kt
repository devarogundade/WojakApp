package dev.arogundade.wojak.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.arogundade.wojak.models.AlertCurrency
import dev.arogundade.wojak.repository.AlertRepository
import dev.arogundade.wojak.utils.WojakNotification
import dev.arogundade.wojak.utils.Extensions.isWatchingPump
import dev.arogundade.wojak.utils.Extensions.smartRound

@HiltWorker
class PriceAlertWorker
@AssistedInject
constructor
    (
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val alertRepository: AlertRepository
) : CoroutineWorker(appContext, params) {

    private val notification = WojakNotification(appContext)

    override suspend fun doWork(): Result {
        try {
            alertRepository.getCurrencies().collect { alertCurrencies ->
                Log.d("Alert", "doWork: $alertCurrencies")
                alertCurrencies.filter { it.alert.active }.forEach { alertCurrency ->
                    val target = alertCurrency.alert.target
                    val price = alertCurrency.currency.price?.toDouble() ?: 0.0

                    if (alertCurrency.alert.isWatchingPump()) {
                        if (price >= target) {
                            pushNotification(alertCurrency, true)
                            alertRepository.disAbleAlert(alertCurrency.alert)
                        }
                    } else {
                        if (price <= target) {
                            pushNotification(alertCurrency, false)
                            alertRepository.disAbleAlert(alertCurrency.alert)
                        }
                    }
                }

            }
            return Result.success()
        } catch (e: Exception) {
            Log.d("Alert", "doWork: $e")
            return Result.retry()
        }
    }

    private fun pushNotification(alertCurrency: AlertCurrency, pump: Boolean) {
        val title = if (pump) "${alertCurrency.currency.name} has pumped to your target price!"
        else "${alertCurrency.currency.name} has dumped to your target price!"

        val description =
            "Target Price: ${smartRound(alertCurrency.alert.target)} - Current Price: ${
                smartRound(alertCurrency.currency.price?.toDouble() ?: 0.0)
            }"

        notification.createNotificationChannel(
            title,
            description,
            alertCurrency.alert.id
        )
    }

}



