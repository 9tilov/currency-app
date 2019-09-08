package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.db.AppDatabase
import com.d9tilov.currencyapp.db.CurrencyLocalRepository
import com.d9tilov.currencyapp.di.scope.ExchangeScope
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.network.RevolutApi
import dagger.Module
import dagger.Provides

@Module
class ExchangeModule {

    @Provides
    @ExchangeScope
    fun provideRemoteRepo(revolutApi: RevolutApi): CurrencyRemoteRepository {
        return CurrencyRemoteRepository(revolutApi)
    }

    @Provides
    @ExchangeScope
    fun provideLocalRepo(appDatabase: AppDatabase): CurrencyLocalRepository {
        return CurrencyLocalRepository(appDatabase)
    }

    @Provides
    @ExchangeScope
    fun provideCurrencyRateInteractor(
        currencyLocalRepository: CurrencyLocalRepository,
        currencyRemoteRepository: CurrencyRemoteRepository
    ): CurrencyRateInteractor {
        return CurrencyRateInteractor(currencyLocalRepository, currencyRemoteRepository)
    }
}
