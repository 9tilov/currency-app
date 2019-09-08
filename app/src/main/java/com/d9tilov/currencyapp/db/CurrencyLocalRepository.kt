package com.d9tilov.currencyapp.db

import androidx.room.withTransaction
import com.d9tilov.currencyapp.db.model.CurrencyData
import javax.inject.Inject

class CurrencyLocalRepository @Inject constructor(private val database: AppDatabase) {

    suspend fun updateCurrencyList(currencyList: List<CurrencyData>) {
        database.withTransaction {
            database.currencyDao().updateCurrencyList(currencyList)
        }
    }

}
