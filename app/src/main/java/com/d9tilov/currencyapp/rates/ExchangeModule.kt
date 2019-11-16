package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.base.Mapper
import com.d9tilov.currencyapp.db.AppDatabase
import com.d9tilov.currencyapp.db.CurrencyLocalRepository
import com.d9tilov.currencyapp.di.scope.ExchangeScope
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.network.RevolutApi
import com.d9tilov.currencyapp.network.data.CurrencyResponse
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.rates.repository.CurrencyResponseMapperImpl
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class ExchangeModule {

    @Provides
    @ExchangeScope
    fun provideRemoteRepo(
        revolutApi: RevolutApi,
        mapper: Mapper<CurrencyResponse, CurrencyRateData>
    ): CurrencyRemoteRepository {
        return CurrencyRemoteRepository(revolutApi, mapper)
    }

    @Provides
    @ExchangeScope
    fun provideRemoteMapper(): Mapper<CurrencyResponse, CurrencyRateData> {
        return CurrencyResponseMapperImpl()
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
        return CurrencyRateInteractor(
            currencyLocalRepository,
            currencyRemoteRepository,
            Schedulers.io(),
            AndroidSchedulers.mainThread()
        )
    }
}
