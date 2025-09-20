package com.example.mada.repository

import com.example.mada.data.datastore.DataStore
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore
) {
    suspend fun setAccount(account: Boolean) = dataStore.setAccount(account)

    suspend fun getAccount() = dataStore.getAccount()

    suspend fun setCard(card: Boolean) = dataStore.setCard(card)

    suspend fun getCard() = dataStore.getCard()

    suspend fun setBudget(
        monday: Int,
        tuesday: Int,
        wednesday: Int,
        thursday: Int,
        friday: Int,
        saturday: Int,
        sunday: Int
    ) = dataStore.setBudget(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

    suspend fun getBudget() = dataStore.getBudget()

    suspend fun setSaveBinder(name: String, targetAmount: String, targetPeriod: String) =
        dataStore.setSaveBinder(name, targetAmount, targetPeriod)

    suspend fun getSaveBinder() = dataStore.getSaveBinder()
}