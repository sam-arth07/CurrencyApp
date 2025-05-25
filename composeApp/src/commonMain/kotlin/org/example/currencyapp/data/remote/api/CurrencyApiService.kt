package org.example.currencyapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.currencyapp.domain.CurrencyApiService
import org.example.currencyapp.domain.PreferenceRepository
import org.example.currencyapp.domain.model.ApiResponse
import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.CurrencyCode
import org.example.currencyapp.domain.model.RequestState
import org.example.currencyapp.domain.model.toCurrency

class CurrencyApiServiceImpl(
    private val preferences: PreferenceRepository
) : CurrencyApiService {
    companion object {
        const val ENDPOINT = "https://api.currencyapi.com/v3/latest"
        const val API_KEY = "cur_live_DmhmqjaV3yUJEhP41tOMcx4LcC6ZYlokFQX02O2o"
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
        }
        install(DefaultRequest) {
            headers {
                append("apiKey", API_KEY)
            }
        }
    }

    override suspend fun getLatestExchangeRates(): RequestState<List<Currency>> {
        return try {
            val response = httpClient.get(ENDPOINT)
            if (response.status.value == 200) {
                val apiResponse = response.body<ApiResponse>()

                //Filter only the currencies that we want, defined in the Currency Code Enum Class
                val availableCurrencyCodes = apiResponse.data.keys
                    .filter {
                        CurrencyCode.entries
                            .map { code -> code.name }
                            .toSet()
                            .contains(it)
                    }
                val availableCurrencies = apiResponse.data.values
                    .filter { currency ->
                        availableCurrencyCodes.contains(currency.code)
                    }.map {
                        it.toCurrency()
                    }
                //Persist a timestamp
                val lastUpdated = apiResponse.meta.lastUpdatedAt
                preferences.saveLastUpdated(lastUpdated)

                RequestState.Success(data = availableCurrencies)
            } else {
                RequestState.Error(message = "HTTP error code: ${response.status}")
            }
        } catch (e: Exception) {
            RequestState.Error(message = e.message.toString())
        }
    }

}