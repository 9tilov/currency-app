package com.d9tilov.currencyapp.view

import android.text.Editable
import android.text.TextWatcher
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class CurrencyTextWatcher : TextWatcher {

    private var subject: PublishSubject<String> = PublishSubject.create()

    override fun afterTextChanged(s: Editable?) {
        Timber.d("afterTextChanged")
        subject.onNext(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    abstract fun onValueChanged(newValue: String)

    init {
        subject
            .debounce(300, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .subscribe({ onValueChanged(it) }, {})
    }
}
