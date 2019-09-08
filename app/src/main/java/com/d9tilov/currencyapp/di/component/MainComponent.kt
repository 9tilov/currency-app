package com.d9tilov.currencyapp.di.component

import com.d9tilov.currencyapp.MainActivity
import com.d9tilov.currencyapp.di.ApplicationProvider
import com.d9tilov.currencyapp.di.ComponentHolder
import dagger.Component

@Component(dependencies = [ApplicationProvider::class])
interface MainComponent {

    fun inject(activity: MainActivity)

    class Initializer private constructor() {
        companion object {
            fun init(): MainComponent {
                return DaggerMainComponent.builder()
                    .applicationProvider(ComponentHolder.provideApplicationProvider())
                    .build()
            }
        }
    }

}