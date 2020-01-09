package com.d9tilov.currencyapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.d9tilov.currencyapp.di.ApplicationProvider
import com.d9tilov.currencyapp.di.ComponentHolder
import com.d9tilov.currencyapp.di.component.AppComponent
import com.facebook.stetho.Stetho
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class App : Application() {

    @Inject
    lateinit var creator: WorkerFactory

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
        Timber.plant(Timber.DebugTree())
        Stetho.initializeWithDefaults(this)
        configureCrashReporting()

        ComponentHolder.provideComponent(ApplicationProvider::class.java.name) {
            AppComponent.Initializer.init(this)
        }.inject(this)
        WorkManager.initialize(this, Configuration.Builder().setWorkerFactory(creator).build())
    }

    private fun configureCrashReporting() {
        val crashlyticsCore = CrashlyticsCore.Builder()
            .disabled(BuildConfig.DEBUG)
            .build()
        Fabric.with(this, Crashlytics.Builder().core(crashlyticsCore).build())
    }
}
