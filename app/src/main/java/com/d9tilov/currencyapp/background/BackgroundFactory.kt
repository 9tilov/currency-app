package com.d9tilov.currencyapp.background

import android.content.Context
import androidx.annotation.MainThread
import androidx.work.WorkManager

private const val JOB_TAG = "currency_update"

object BackgroundFactory {
    @MainThread
    fun enequeueJob(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(JOB_TAG)

    }
}