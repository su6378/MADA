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

    suspend fun setBudgetExist(exist: Boolean) = dataStore.setBudgetExist(exist)

    suspend fun getBudgetExist() = dataStore.getBudgetExist()

    suspend fun setBudget(
        monday: Int = 0,
        tuesday: Int = 0,
        wednesday: Int = 0,
        thursday: Int = 0,
        friday: Int = 0,
        saturday: Int = 0,
        sunday: Int = 0
    ) = dataStore.setBudget(monday, tuesday, wednesday, thursday, friday, saturday, sunday)

    suspend fun getBudget() = dataStore.getBudget()

    suspend fun setSaveBinder(name: String, targetAmount: String, targetPeriod: String) =
        dataStore.setSaveBinder(name, targetAmount, targetPeriod)

    suspend fun getSaveBinder() = dataStore.getSaveBinder()

    suspend fun setStep(step: Int) = dataStore.setStep(step)

    suspend fun getStep() = dataStore.getStep()
}