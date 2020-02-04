package com.d9tilov.currencyapp.storage

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.model.CurrencyDto
import com.d9tilov.currencyapp.utils.CurrencyUtils.getCurrencyFullName
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.math.BigDecimal
import java.util.concurrent.Executor

class CurrencyLocalRepository(
    private val database: AppDatabase,
    private val currencySharedPreferences: CurrencySharedPreferences,
    private val executor: Executor
) {

    private val localCopyOfCurrencyList: MutableList<CurrencyItem> =
        mutableListOf()

    @WorkerThread
    fun updateCurrencyList(
        currencyRateData: CurrencyRateData
    ): Observable<CurrencyRateData> {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseValue = currencySharedPreferences.loadBaseCurrency().value
        for (item in currencyRateData.currencyList) {
            val newValue = baseValue * item.value
            currencyRateList.add(CurrencyDto(item.name, newValue.toString()))
        }
        database.currencyDao().updateCurrencyList(currencyRateList)
        return Observable.fromCallable { currencyRateData }
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyItem>> {
        return database.currencyDao().getCurrencyList()
            .doOnNext {
                localCopyOfCurrencyList.clear()
            }
            .concatMapSingle { list ->
                Flowable.fromIterable(list)
                    .map { convert(it) }
                    .doOnNext {
                        localCopyOfCurrencyList.add(it)
                    }
                    .toSortedList(compareBy({ !it.isBase }, { it.name }))
            }
    }

    private fun convert(currencyDto: CurrencyDto): CurrencyItem {
        val baseCurrency = currencySharedPreferences.loadBaseCurrency()
        return CurrencyItem(
            currencyDto.name,
            currencyDto.value.toBigDecimal(),
            baseCurrency.name == currencyDto.name
        )
    }

    fun changeBaseCurrency(baseCurrency: CurrencyItem): Single<List<CurrencyItem>> {
        currencySharedPreferences.saveBaseCurrency(baseCurrency)
        return recalculateCurrency()
    }

    fun changeValue(newBaseItem: CurrencyItem): Single<List<CurrencyItem>> {
        currencySharedPreferences.saveBaseCurrency(newBaseItem)
        return recalculateCurrency()
    }

    private fun recalculateCurrency(): Single<List<CurrencyItem>> {
        val baseCurrency = currencySharedPreferences.loadBaseCurrency()
        if (baseCurrency.value.compareTo(BigDecimal.ZERO) == 0) {
            return Single.fromCallable { localCopyOfCurrencyList }
        }
        val oldValue =
            localCopyOfCurrencyList.find { baseCurrency.name == it.name }?.value ?: BigDecimal.ONE
        val tmpList = mutableListOf<CurrencyItem>()
        for (item in localCopyOfCurrencyList) {
            val newRecountedValue = item.value * baseCurrency.value / oldValue
            tmpList.add(CurrencyItem(item.name, newRecountedValue, item.name == baseCurrency.name))
        }
        localCopyOfCurrencyList.clear()
        localCopyOfCurrencyList.addAll(tmpList)
        sortCurrency()
        return Single.fromCallable { localCopyOfCurrencyList }
    }

    private fun sortCurrency() {
        localCopyOfCurrencyList.sortWith(compareBy({ !it.isBase }, { getCurrencyFullName(it.name) }))
    }

    fun flushData() {
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            currencyRateList.add(CurrencyDto(item.name, item.value.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }
}
