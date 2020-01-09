package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.di.scope.ExchangeScope
import com.d9tilov.currencyapp.network.CurrencyRemoteRepository
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class ExchangeModule {

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
