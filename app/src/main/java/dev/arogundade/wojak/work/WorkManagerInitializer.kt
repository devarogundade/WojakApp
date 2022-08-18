package dev.arogundade.wojak.work

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

class WorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val configuration = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        WorkManager.initialize(context, configuration)
        return WorkManager.getInstance(context)
    }

    // This component does not have any dependencies
    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}