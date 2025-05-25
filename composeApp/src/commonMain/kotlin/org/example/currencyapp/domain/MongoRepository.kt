package org.example.currencyapp.domain

import kotlinx.coroutines.flow.Flow
import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.RequestState

interface MongoRepository {
    fun configureTheRealm()
    suspend fun insertCurrencyData(currency: Currency)
    fun readCurrencyData(): Flow<RequestState<List<Currency>>>
    suspend fun cleanUp()

}