package com.d9tilov.currencyapp.view

import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.math.BigDecimal

object CurrencyTextWatcher {

    fun fromView(editText: EditText): Observable<BigDecimal> {
        val subject: BehaviorSubject<BigDecimal> = BehaviorSubject.create()
        editText.addTextChangedListener {
            val number = it.toString()
            if (!it.isNullOrEmpty() && number.toBigDecimalOrNull() != null && number.toBigDecimal().compareTo(
                    BigDecimal.ZERO
                ) != 0
            ) {
                subject.onNext(it.toString().toBigDecimal())
            } else {
                subject.onNext(BigDecimal.ZERO)
            }
        }
        return subject
    }
}
