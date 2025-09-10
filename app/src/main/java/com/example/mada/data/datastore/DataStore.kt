package com.example.mada.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
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
    private val budgetPreference = intPreferencesKey("BUDGET")
    private val mondayPreference = intPreferencesKey("MONDAY")
    private val tuesdayPreference = intPreferencesKey("TUESDAY")
    private val wednesdayPreference = intPreferencesKey("WEDNESDAY")
    private val thursdayPreference = intPreferencesKey("THURSDAY")
    private val fridayPreference = intPreferencesKey("FRIDAY")
    private val saturdayPreference = intPreferencesKey("SATURDAY")
    private val sundayPreference = intPreferencesKey("SUNDAY")

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

    suspend fun setBudget(
        monday: Int,
        tuesday: Int,
        wednesday: Int,
        thursday: Int,
        friday: Int,
        saturday: Int,
        sunday: Int
    ) {
        context.dataStore.edit { preference ->
            preference[mondayPreference] = monday
            preference[tuesdayPreference] = tuesday
            preference[wednesdayPreference] = wednesday
            preference[thursdayPreference] = thursday
            preference[fridayPreference] = friday
            preference[saturdayPreference] = saturday
            preference[sundayPreference] = sunday
        }
    }

    suspend fun getBudget(): Flow<List<Int>> {
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
                arrayListOf(
                    prefs[mondayPreference] ?: 0,
                    prefs[tuesdayPreference] ?: 0,
                    prefs[wednesdayPreference] ?: 0,
                    prefs[thursdayPreference] ?: 0,
                    prefs[fridayPreference] ?: 0,
                    prefs[saturdayPreference] ?: 0,
                    prefs[sundayPreference] ?: 0,
                )
            }
    }
}