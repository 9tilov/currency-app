package com.d9tilov.currencyapp.di

import android.content.Context
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.network.RevolutApi
import com.d9tilov.currencyapp.storage.AppDatabase
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository

interface ApplicationProvider {

    fun provideContext(): Context
    fun provideStorage(): AppDatabase
    fun provideApi(): RevolutApi
    fun provideLocalRepo(): CurrencyLocalRepository
    fun provideRemoteRepo(): CurrencyRemoteRepository
}
