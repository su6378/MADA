package com.example.mada.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
    private val cardPreference = booleanPreferencesKey("CARD")
    private val mondayPreference = intPreferencesKey("MONDAY")
    private val tuesdayPreference = intPreferencesKey("TUESDAY")
    private val wednesdayPreference = intPreferencesKey("WEDNESDAY")
    private val thursdayPreference = intPreferencesKey("THURSDAY")
    private val fridayPreference = intPreferencesKey("FRIDAY")
    private val saturdayPreference = intPreferencesKey("SATURDAY")
    private val sundayPreference = intPreferencesKey("SUNDAY")
    private val saveBinderPreference = stringSetPreferencesKey("SAVE")
    private val stepPreference = intPreferencesKey("STEP")
    private val budgetExistPreference = booleanPreferencesKey("EXIST")
    private val budgetBinderImagePreference = stringPreferencesKey("BUDGET_IMAGE")
    private val saveBinderImagePreference = stringPreferencesKey("SAVE_IMAGE")

    private val oneBudgetPreference = stringSetPreferencesKey("ONE")

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

    suspend fun setCard(card: Boolean) {
        context.dataStore.edit { preference ->
            preference[cardPreference] = card
        }
    }

    suspend fun getCard(): Flow<Boolean> {
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
                prefs[cardPreference] ?: false
            }
    }

    suspend fun setBudgetExist(exist: Boolean) {
        context.dataStore.edit { preference ->
            preference[budgetExistPreference] = exist
        }
    }

    suspend fun getBudgetExist(): Flow<Boolean> {
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
                prefs[budgetExistPreference] ?: false
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

    suspend fun setSaveBinder(
        name: String,
        targetAmount: String,
        startPeriod: String,
        targetPeriod: String
    ) {
        context.dataStore.edit { preference ->
            preference[saveBinderPreference] = setOf(name, targetAmount, targetPeriod)
        }
    }

    suspend fun getSaveBinder(): Flow<Set<String>> {
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
                prefs[saveBinderPreference] ?: emptySet()
            }
    }

    suspend fun setStep(
        step: Int
    ) {
        context.dataStore.edit { preference ->
            preference[stepPreference] = step
        }
    }

    suspend fun getStep(): Flow<Int> {
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
                prefs[stepPreference] ?: 0
            }
    }

    suspend fun setBudgetBinderImage(
        budgetBinderImage: String
    ) {
        context.dataStore.edit { preference ->
            preference[budgetBinderImagePreference] = budgetBinderImage
        }
    }

    suspend fun getBudgetBinderImage(): Flow<String> {
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
                prefs[budgetBinderImagePreference] ?: ""
            }
    }

    suspend fun setSaveBinderImage(
        saveBinderImage: String
    ) {
        context.dataStore.edit { preference ->
            preference[saveBinderImagePreference] = saveBinderImage
        }
    }

    suspend fun getSaveBinderImage(): Flow<String> {
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
                prefs[saveBinderImagePreference] ?: ""
            }
    }
}