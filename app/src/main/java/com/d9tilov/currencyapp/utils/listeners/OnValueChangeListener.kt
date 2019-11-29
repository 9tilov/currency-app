package com.d9tilov.currencyapp.utils.listeners

interface OnValueChangeListener<in T, in V> {
    fun onValueChanged(item: T, value: V, position: Int)
}
