package com.d9tilov.currencyapp.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencyData")
data class CurrencyData(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "locale") val token: String,
    @ColumnInfo(name = "value") val name: Int
)
