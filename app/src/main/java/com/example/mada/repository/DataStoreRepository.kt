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
}