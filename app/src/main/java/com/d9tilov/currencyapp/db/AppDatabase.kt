package com.d9tilov.currencyapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.d9tilov.currencyapp.db.model.CurrencyData

@Database(entities = [CurrencyData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
}
