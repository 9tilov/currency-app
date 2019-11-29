package com.d9tilov.currencyapp.utils.listeners

interface OnItemClickListener<in T> {
    fun onItemClick(item: T, position: Int)
}
