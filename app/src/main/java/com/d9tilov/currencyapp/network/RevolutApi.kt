package com.d9tilov.currencyapp.network

import com.d9tilov.currencyapp.core.Constants
import com.d9tilov.currencyapp.network.data.CurrencyResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RevolutApi {

    @GET("latest")
    fun getCurrencies(@Query("base") baseCurrency: String): Single<CurrencyResponse>

    companion object Factory {
        fun create(): RevolutApi {
            val retrofit = retrofit2.Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .build()

            return retrofit.create(RevolutApi::class.java)
        }
    }
}
