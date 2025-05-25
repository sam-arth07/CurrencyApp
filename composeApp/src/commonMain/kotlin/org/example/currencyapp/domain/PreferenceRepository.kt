package org.example.currencyapp.domain

import kotlinx.coroutines.flow.Flow
import org.example.currencyapp.domain.model.CurrencyCode

interface PreferenceRepository {
    suspend fun saveLastUpdated(lastUpdated: String)
    suspend fun isDataFresh(currentTimestamp: Long): Boolean
    suspend fun saveSourceCurrencyCode(code: String)
    suspend fun saveTargetCurrencyCode(code: String)
    fun readSourceCurrencyCode(): Flow<CurrencyCode>
    fun readTargetCurrencyCode(): Flow<CurrencyCode>
}