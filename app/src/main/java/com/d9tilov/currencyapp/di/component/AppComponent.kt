package com.d9tilov.currencyapp.di.component

import android.content.Context
import com.d9tilov.currencyapp.MainActivity
import com.d9tilov.currencyapp.db.StorageModule
import com.d9tilov.currencyapp.di.ApplicationProvider
import com.d9tilov.currencyapp.di.module.ContextModule
import com.d9tilov.currencyapp.network.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ContextModule::class,
        NetworkModule::class,
        StorageModule::class]
)
interface AppComponent : ApplicationProvider {

    fun inject(activity: MainActivity)

    class Initializer private constructor() {
        companion object {
            fun init(context: Context): AppComponent {
                return DaggerAppComponent.builder()
                    .contextModule(ContextModule(context))
                    .storageModule(StorageModule())
                    .networkModule(NetworkModule())
                    .build()
            }
        }
    }
}
