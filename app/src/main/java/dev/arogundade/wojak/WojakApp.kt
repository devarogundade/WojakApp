package dev.arogundade.wojak

import android.app.Application
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.arogundade.wojak.work.PriceAlertWorkerFactory
import dev.arogundade.wojak.work.WojakWork
import javax.inject.Inject

@HiltAndroidApp
class WojakApp : Application(), Configuration.Provider {

    @Inject
    lateinit var factory: PriceAlertWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(factory)
            .build()

    override fun onCreate() {
        super.onCreate()
        WojakWork.setupPriceAlertWork(this)
    }
}