package com.d9tilov.currencyapp.network

import io.reactivex.subjects.PublishSubject

interface RetryManager {
    fun observeRetries(): PublishSubject<RetryEvent>
    fun retry()
}