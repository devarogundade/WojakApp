package dev.arogundade.wojak.work

import android.content.Context
import androidx.startup.AppInitializer
import androidx.work.*
import dev.arogundade.wojak.utils.Constants.WORK_NAME
import java.util.concurrent.TimeUnit

object WojakWork {

    /* worker interval to every 15 minutes */
    private const val INTERVAL = 15L

    /* worker limited to internet only */
    private val constraint = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    fun setupPriceAlertWork(context: Context) {
        val request = PeriodicWorkRequestBuilder<PriceAlertWorker>(INTERVAL, TimeUnit.MINUTES)
            .setConstraints(constraint)
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request)
    }

}