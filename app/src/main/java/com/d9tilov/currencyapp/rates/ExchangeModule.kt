package com.d9tilov.currencyapp.rates

import android.content.Context
import android.content.SharedPreferences
import com.d9tilov.currencyapp.di.scope.ExchangeScope
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.network.RevolutApi
import com.d9tilov.currencyapp.storage.AppDatabase
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import com.d9tilov.currencyapp.storage.CurrencySharedPreferences
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Module
class ExchangeModule {

    @Provides
    @ExchangeScope
    fun provideRemoteRepo(
        revolutApi: RevolutApi,
        sharedPreferences: CurrencySharedPreferences
    ): CurrencyRemoteRepository {
        return CurrencyRemoteRepository(revolutApi, sharedPreferences)
    }

    @Provides
    @ExchangeScope
    fun provideLocalRepo(
        appDatabase: AppDatabase,
        sharedPreferences: CurrencySharedPreferences,
        executor: Executor
    ):
            CurrencyLocalRepository {
        return CurrencyLocalRepository(appDatabase, sharedPreferences, executor)
    }

    @Provides
    @ExchangeScope
    fun provideExecutor(): Executor {
        return Executors.newFixedThreadPool(2)
    }

    @Provides
    @ExchangeScope
    fun provideSharedPreferences(context: Context):
            SharedPreferences {
        return context.getSharedPreferences("storage", Context.MODE_PRIVATE)
    }

    @Provides
    @ExchangeScope
    fun provideCurrencyPreferences(sharedPreferences: SharedPreferences):
            CurrencySharedPreferences {
        return CurrencySharedPreferences(sharedPreferences)
    }

    @Provides
    @ExchangeScope
    fun provideCurrencyRateInteractor(
        currencyLocalRepository: CurrencyLocalRepository,
        currencyRemoteRepository: CurrencyRemoteRepository
    ): CurrencyRateInteractor {
        return CurrencyRateInteractor(
            currencyLocalRepository,
            currencyRemoteRepository,
            Schedulers.io(),
            AndroidSchedulers.mainThread()
        )
    }
}
