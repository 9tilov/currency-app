package com.d9tilov.currencyapp.background

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class BackgroundUpdateWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    lateinit var currencyLocalRepository: CurrencyLocalRepository
    lateinit var currencyRemoteRepository: CurrencyRemoteRepository

    @WorkerThread
    override fun doWork(): Result {
        Timber.d("background task: start periodic task")
        currencyRemoteRepository.updateCurrencyRates()
            .flatMap { t: CurrencyRateData ->
                currencyLocalRepository.updateCurrencyList(t)
            }
            .subscribeOn(Schedulers.io())
            .subscribe()
        return Result.success()
    }
}
