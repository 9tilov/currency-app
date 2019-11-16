package com.d9tilov.currencyapp.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.d9tilov.currencyapp.db.model.CurrencyDto
import io.reactivex.Single

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateCurrencyList(currencyDtoList: List<CurrencyDto>)

    @Query("SELECT * FROM currencyData")
    fun getCurrencyList(): Single<List<CurrencyDto>>

}
