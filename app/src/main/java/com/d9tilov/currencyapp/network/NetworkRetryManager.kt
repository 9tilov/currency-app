package com.d9tilov.currencyapp.network

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class NetworkRetryManager : RetryManager {

    private val retrySubject = PublishSubject.create<RetryEvent>()

    override fun observeRetries(error: Throwable): Observable<RetryEvent> {
        return retrySubject
    }

    override fun retry() {
        retrySubject.onNext(RetryEvent())
    }
}