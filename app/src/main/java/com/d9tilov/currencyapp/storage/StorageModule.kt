package com.d9tilov.currencyapp.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.d9tilov.currencyapp.core.Constants.Companion.DB
import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import java.util.concurrent.Executors
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

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context):
            SharedPreferences {
        return context.getSharedPreferences("storage", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideCurrencyPreferences(sharedPreferences: SharedPreferences):
            CurrencySharedPreferences {
        return CurrencySharedPreferences(sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executors.newFixedThreadPool(2)
    }

    @Provides
    @Singleton
    fun provideLocalRepo(
        appDatabase: AppDatabase,
        sharedPreferences: CurrencySharedPreferences,
        executor: Executor
    ):
            CurrencyLocalRepository {
        return CurrencyLocalRepository(appDatabase, sharedPreferences, executor)
    }
}
