package com.d9tilov.currencyapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.d9tilov.currencyapp.db.model.CurrencyData
import io.reactivex.Single

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrencyList(currencyDataList: List<CurrencyData>)

    @Query("SELECT * FROM currencyData")
    fun getCurrencyList(): Single<List<CurrencyData>>

}
