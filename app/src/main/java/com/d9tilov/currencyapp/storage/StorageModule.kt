package com.d9tilov.currencyapp.storage

import android.content.Context
import androidx.room.Room
import com.d9tilov.currencyapp.core.Constants.Companion.DB
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Provides
    @Singleton
    fun provideAppDataBase(context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, DB)
            .build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }
}
