package com.d9tilov.currencyapp.network

import androidx.work.WorkerFactory
import com.d9tilov.currencyapp.background.DaggerWorkerFactory
import com.d9tilov.currencyapp.core.Constants
import com.d9tilov.currencyapp.storage.CurrencyLocalRepository
import com.d9tilov.currencyapp.storage.CurrencySharedPreferences
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.d(message)
                }
            })
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        return httpLoggingInterceptor
    }

    @Provides
    @Singleton
    fun provideStethoInterceptor(): StethoInterceptor {
        return StethoInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttp(
        interceptor: HttpLoggingInterceptor,
        stethoInterceptor: StethoInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .addNetworkInterceptor(stethoInterceptor)
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): RevolutApi {
        return retrofit
            .create(RevolutApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteRepo(
        revolutApi: RevolutApi,
        sharedPreferences: CurrencySharedPreferences
    ): CurrencyRemoteRepository {
        return CurrencyRemoteRepository(revolutApi, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideWorkerFactory(
        currencyRemoteRepository: CurrencyRemoteRepository,
        currencyLocalRepository: CurrencyLocalRepository
    ): WorkerFactory {
        return DaggerWorkerFactory(currencyRemoteRepository, currencyLocalRepository)
    }
}
