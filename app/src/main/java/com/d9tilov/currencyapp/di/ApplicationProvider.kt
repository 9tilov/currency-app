package com.d9tilov.currencyapp.di

import android.content.Context
import com.d9tilov.currencyapp.network.RevolutApi
import com.d9tilov.currencyapp.storage.AppDatabase

interface ApplicationProvider {

    fun provideContext(): Context
    fun provideStorage(): AppDatabase
    fun provideApi(): RevolutApi
}
