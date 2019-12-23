package com.d9tilov.currencyapp

import android.os.Bundle
import com.d9tilov.currencyapp.base.BaseActivity
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.di.component.MainComponent
import com.d9tilov.currencyapp.rates.ExchangeRatesFragment
import timber.log.Timber

class MainActivity : BaseActivity() {

    override fun getFragmentContainerId(): Int {
        return R.id.main_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.d("MainActivity")
        ComponentHolder.provideComponent(MainComponent::class.java.name) {
            MainComponent.Initializer.init()
        }.inject(this)

        val fragment = ExchangeRatesFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .add(getFragmentContainerId(), fragment, fragment.fragmentTag)
            .commit()
    }
}
