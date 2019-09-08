package com.d9tilov.currencyapp.network

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {

//    @Provides
//    @Singleton
//    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
//        val httpLoggingInterceptor = HttpLoggingInterceptor { message -> Timber.d(message) }
//        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
//        return httpLoggingInterceptor
//    }
//
//    @Provides
//    @Singleton
//    fun provideStethoInterceptor(): StethoInterceptor {
//        return StethoInterceptor()
//    }
//
//    @Provides
//    @Singleton
//    fun provideOkHttp(
//        interceptor: HttpLoggingInterceptor,
//        stethoInterceptor: StethoInterceptor
//    ): OkHttpClient {
//        return OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS)
//            .connectTimeout(15, TimeUnit.SECONDS)
//            .addNetworkInterceptor(stethoInterceptor)
//            .addInterceptor(interceptor)
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideGson(): Gson {
//        return GsonBuilder()
//            .setLenient()
//            .create()
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
//        return Retrofit.Builder()
//            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .baseUrl(Constants.BASE_URL)
//            .client(client)
//            .build()
//    }

    @Provides
    @Singleton
    fun provideApiService(): RevolutApi {
        return RevolutApi.create()
    }
}