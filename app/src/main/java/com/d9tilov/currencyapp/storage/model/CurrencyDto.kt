package com.d9tilov.currencyapp.storage.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencyData")
data class CurrencyDto(
    @PrimaryKey val name: String,
    @ColumnInfo(name = "value") val value: String
)
