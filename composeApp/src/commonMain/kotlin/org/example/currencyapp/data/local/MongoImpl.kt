package org.example.currencyapp.data.local

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.example.currencyapp.domain.MongoRepository
import org.example.currencyapp.domain.model.Currency
import org.example.currencyapp.domain.model.RequestState

class MongoImpl : MongoRepository {
    private var realm: Realm? = null

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        //This function needs to be called as soon as we initialize the class
        if (realm == null || realm!!.isClosed()) {
            val config = RealmConfiguration.Builder(
                schema = setOf(Currency::class)
            )
                .compactOnLaunch()
                .build()
            realm = Realm.open(config)
        }
    }

    override suspend fun insertCurrencyData(currency: Currency) {
        realm?.write { copyToRealm(currency) }
    }

    override fun readCurrencyData(): Flow<RequestState<List<Currency>>> {
        return realm?.query<Currency>()
            ?.asFlow()
            ?.map { result ->
                RequestState.Success(result.list)
            }
            ?: flow { RequestState.Error(message = "Realm not Configured") }
    }

    override suspend fun cleanUp() {
        realm?.write {
            val currencyCollection = this.query<Currency>()
            delete(currencyCollection)
        }
    }

}