package dev.arogundade.wojak.work

import android.content.Context
import androidx.annotation.NonNull
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.arogundade.wojak.repository.AlertRepository
import javax.inject.Inject

class PriceAlertWorkerFactory
@Inject
constructor(
    private val alertRepository: AlertRepository
) : WorkerFactory() {

    @NonNull
    override fun createWorker(
        @NonNull appContext: Context,
        @NonNull workerClassName: String,
        @NonNull workerParameters: WorkerParameters
    ): ListenableWorker {
        return PriceAlertWorker(appContext, workerParameters, alertRepository)
    }

}