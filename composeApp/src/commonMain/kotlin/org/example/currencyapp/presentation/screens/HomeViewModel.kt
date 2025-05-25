package org.example.currencyapp.presentation.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import org.example.currencyapp.domain.CurrencyApiService
import org.example.currencyapp.domain.MongoRepository
import org.example.currencyapp.domain.PreferenceRepository
import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.RateStatus
import org.example.currencyapp.domain.model.RequestState

/*
* To send custom events from the Ui and trigger them inside the ViewModel
* */
sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
    data class SaveSourceCurrencyCode(val code: String) : HomeUiEvent()
    data class SaveTargetCurrencyCode(val code: String) : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferenceRepository,
    private val mongoDb: MongoRepository,
    private val api: CurrencyApiService
) : ScreenModel {
    private var _rateStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val rateStatus: State<RateStatus> = _rateStatus

    private var _sourceCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> =
        mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    //sendEvent should be triggered from the UI from where those events are sent to the ViewModel
    fun sendEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }

            HomeUiEvent.SwitchCurrencies -> {
                switchCurrencies()
            }

            is HomeUiEvent.SaveSourceCurrencyCode -> {
                saveSourceCurrencyCode(event.code)
            }

            is HomeUiEvent.SaveTargetCurrencyCode -> {
                saveTargetCurrencyCode(event.code)
            }
        }
    }

    private fun saveSourceCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveSourceCurrencyCode(code)
        }
    }

    private fun saveTargetCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveTargetCurrencyCode(code)
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCountry = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCountry != null) {
                    _sourceCurrency.value = RequestState.Success(selectedCountry)
                } else {
                    _sourceCurrency.value =
                        RequestState.Error(message = "Couldn't find the currency.")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCountry = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCountry != null) {
                    _targetCurrency.value = RequestState.Success(selectedCountry)
                } else {
                    _targetCurrency.value =
                        RequestState.Error(message = "Couldn't find the currency.")
                }
            }
        }
    }


    private suspend fun getRatesStatus() {
        _rateStatus.value = if (preferences.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        )
            RateStatus.Fresh
        else RateStatus.Stale
    }

    private suspend fun cacheLogic() {
        val fetchedData = api.getLatestExchangeRates()
        if (fetchedData.isSuccess()) {
            mongoDb.cleanUp()
            fetchedData.getSuccessData().forEach {
                println("HomeViewModel: Adding ${it.code}")
                mongoDb.insertCurrencyData(it)
            }
            println("HomeViewModel : Updating _allCurrencies")
            _allCurrencies.clear()
            _allCurrencies.addAll(fetchedData.getSuccessData())
        } else if (fetchedData.isError()) {
            println("HomeViewModel: Fetching Failed ${fetchedData.getErrorMessage()}")
        }
    }

    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoDb.readCurrencyData().first()
            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HOMEVIEWMODEL : Database Is Full")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData())
                    if (!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())) {
                        //Local Db has outdated data
                        println("HOMEVIEWMODEL : Data is not Fresh")
                        cacheLogic()
                    } else {
                        //Local db has correct fresh data
                        println("HomeViewModel: Data is Fresh")
                    }
                } else {
                    // When Local Database is Empty
                    println("HomeViewModel: Database Needs Data")
                    cacheLogic()
                }
            } else if (localCache.isError()) {
                // When Failed to read the Local Database
                println("HomeViewModel: Error reading local database ${localCache.getErrorMessage()}")
            }
            getRatesStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun switchCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }
}