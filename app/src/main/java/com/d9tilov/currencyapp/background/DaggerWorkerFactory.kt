package com.d9tilov.currencyapp.background

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository

class DaggerWorkerFactory(
    private val currencyRemoteRepository: CurrencyRemoteRepository,
    private val currencyLocalRepository: CurrencyLocalRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): Worker? {

        val workerKlass = Class.forName(workerClassName).asSubclass(Worker::class.java)
        val constructor =
            workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is BackgroundUpdateWorker -> {
                instance.currencyLocalRepository = currencyLocalRepository
                instance.currencyRemoteRepository = currencyRemoteRepository
            }
        }

        return instance
    }
}