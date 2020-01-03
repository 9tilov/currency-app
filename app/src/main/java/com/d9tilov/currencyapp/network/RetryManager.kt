package com.d9tilov.currencyapp.network

import io.reactivex.Observable

interface RetryManager {
    fun observeRetries(error: Throwable): Observable<RetryEvent>
    fun retry()
}