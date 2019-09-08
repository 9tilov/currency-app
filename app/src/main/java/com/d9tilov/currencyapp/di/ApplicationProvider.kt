package com.d9tilov.currencyapp.di

import android.content.Context
import com.d9tilov.currencyapp.db.AppDatabase
import com.d9tilov.currencyapp.network.RevolutApi

interface ApplicationProvider {

    fun provideContext(): Context
    fun provideStorage(): AppDatabase
    fun provideApi(): RevolutApi
}
