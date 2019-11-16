package com.d9tilov.currencyapp.db

import androidx.room.withTransaction
import com.d9tilov.currencyapp.db.model.CurrencyDto

class CurrencyLocalRepository(private val database: AppDatabase) {

    suspend fun updateCurrencyList(currencyList: List<CurrencyDto>) {
        database.withTransaction {
            database.currencyDao().updateCurrencyList(currencyList)
        }
    }

}
