package com.example.mada.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStore(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore("MADA")
    private val accountPreference = booleanPreferencesKey("ACCOUNT")

    suspend fun setAccount(account: Boolean) {
        context.dataStore.edit { preference ->
            preference[accountPreference] = account
        }
    }

    suspend fun getAccount(): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[accountPreference] ?: false
            }
    }
}