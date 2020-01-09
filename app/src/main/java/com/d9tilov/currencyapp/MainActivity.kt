package com.d9tilov.currencyapp

import android.os.Bundle
import com.d9tilov.currencyapp.background.BackgroundFactory
import com.d9tilov.currencyapp.base.BaseActivity
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.di.component.MainComponent
import com.d9tilov.currencyapp.rates.ExchangeRatesFragment

class MainActivity : BaseActivity() {

    override fun getFragmentContainerId(): Int {
        return R.id.main_container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ComponentHolder.provideComponent(MainComponent::class.java.name) {
            MainComponent.Initializer.init()
        }.inject(this)
        BackgroundFactory.enqueueJob(this)
        val fragment = supportFragmentManager.findFragmentByTag(ExchangeRatesFragment.TAG)
            ?: ExchangeRatesFragment.newInstance()
        supportFragmentManager
            .beginTransaction()
            .replace(getFragmentContainerId(), fragment, ExchangeRatesFragment.TAG)
            .commit()
    }
}
