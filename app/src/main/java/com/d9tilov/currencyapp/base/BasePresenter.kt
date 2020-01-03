package com.d9tilov.currencyapp.base

import com.d9tilov.currencyapp.network.NetworkRetryManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<V : BaseView> {

    private var view: V? = null
    private val disposable = CompositeDisposable()
    val retryManager = NetworkRetryManager()

    open fun unSubscribeOnDetach(vararg disposables: Disposable) {
        disposable.addAll(*disposables)
    }

    fun attachView(view: V) {
        this.view = view
        onViewAttached(view)
    }

    open fun onViewAttached(view: V) {}

    fun retryCall() {
        retryManager.retry()
    }

    fun view(action: V.() -> Unit) {
        view?.run(action)
    }


    fun detachView() {
        disposable.clear()
        view = null
    }
}