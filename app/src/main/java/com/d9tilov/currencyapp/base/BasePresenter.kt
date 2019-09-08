package com.d9tilov.currencyapp.base

abstract class BasePresenter<V : BaseView> {

    private var view: V? = null

    fun attachView(view: V) {
        this.view = view
        onViewAttached(view)
    }

    open fun onViewAttached(view: V) {}


    fun view(action: V.() -> Unit) {
        view?.run(action)
    }


    fun detachView() {
        view = null
    }
}