package org.example.currencyapp.domain

import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.RequestState

interface CurrencyApiService {
    suspend fun getLatestExchangeRates(): RequestState<List<Currency>>
}