package com.d9tilov.currencyapp.storage

import androidx.annotation.WorkerThread
import com.d9tilov.currencyapp.rates.repository.CurrencyItem
import com.d9tilov.currencyapp.rates.repository.CurrencyRateData
import com.d9tilov.currencyapp.storage.model.CurrencyDto
import io.reactivex.Flowable
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
    fun updateCurrencyList(currencyRateData: CurrencyRateData) {
        val currencyRateList = mutableListOf<CurrencyDto>()
        val baseItem = currencyRateData.currencyList.find { it.isBase }
            ?: throw IllegalAccessException("Can't find base item")
        val baseValue = baseItem.value
        for (item in currencyRateData.currencyList) {
            val newValue = baseValue * item.value
            currencyRateList.add(CurrencyDto(item.name, newValue.toString()))
        }
        database.currencyDao().updateCurrencyList(currencyRateList)
    }

    @WorkerThread
    fun getAllCurrencies(): Flowable<List<CurrencyItem>> {
        return database.currencyDao().getCurrencyList()
            .doOnNext {
                localCopyOfCurrencyList.clear()
            }
            .flatMapSingle { list ->
                Flowable.fromIterable(list)
                    .map { convert(it) }
                    .doOnNext {
                        localCopyOfCurrencyList.add(it)
                    }
                    .toList()
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

    fun updateBaseCurrency(baseCurrency: CurrencyItem) {
        currencySharedPreferences.saveBaseCurrency(baseCurrency)
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        localCopyOfCurrencyList[0].isBase = false
        val oldValue =
            localCopyOfCurrencyList
                .find { it.name == baseCurrency.name }
                ?.value
                ?: return
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return
        }
        localCopyOfCurrencyList.find { it.name == baseCurrency.name }?.isBase = true
        sortCurrency()
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            val newValue = item.value / oldValue
            currencyRateList.add(CurrencyDto(item.name, newValue.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    fun changeValue(newValue: BigDecimal) {
        if (localCopyOfCurrencyList.isEmpty()) {
            return
        }
        val oldValue = localCopyOfCurrencyList[0].value
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            val newRecountedValue = item.value * newValue / oldValue
            currencyRateList.add(CurrencyDto(item.name, newRecountedValue.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }

    private fun sortCurrency() {
        localCopyOfCurrencyList.sortWith(compareBy({ !it.isBase }, { it.name }))
    }

    fun cancelWorking() {
        val currencyRateList = mutableListOf<CurrencyDto>()
        for (item in localCopyOfCurrencyList) {
            currencyRateList.add(CurrencyDto(item.name, item.value.toString()))
        }
        executor.execute { database.currencyDao().updateCurrencyList(currencyRateList) }
    }
}
