package com.d9tilov.currencyapp.rates

import com.d9tilov.currencyapp.di.ApplicationProvider
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.di.scope.ExchangeScope
import dagger.Component

@ExchangeScope
@Component(
    dependencies = [ApplicationProvider::class],
    modules = [ExchangeModule::class]
)
interface ExchangeComponent {

    fun inject(fragment: ExchangeRatesFragment)

    class Initializer private constructor() {
        companion object {
            fun init(): ExchangeComponent {
                return DaggerExchangeComponent.builder()
                    .applicationProvider(ComponentHolder.provideApplicationProvider())
                    .build()
            }
        }
    }
}