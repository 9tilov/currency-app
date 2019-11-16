package com.d9tilov.currencyapp.base

interface Mapper<I, O> {
    fun convertFrom(input: I): O
}
