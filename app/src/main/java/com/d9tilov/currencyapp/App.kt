package com.d9tilov.currencyapp

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.d9tilov.currencyapp.di.ApplicationProvider
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.di.component.AppComponent
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
        configureCrashReporting()

        ComponentHolder.provideComponent(ApplicationProvider::class.java.name) {
            AppComponent.Initializer.init(this)
        }
    }

    private fun configureCrashReporting() {
        val crashlyticsCore = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())
    }
}
