package com.example.gymapp.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.planDataStore: DataStore<Preferences> by preferencesDataStore(name = "plan_attributes")

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

object DataStoreHelper {

    private val DAYS_KEY = stringPreferencesKey("days")
    private val WEEKS_KEY = stringPreferencesKey("weeks")

    private val USER_KEY = stringPreferencesKey("username")
    private val PSS_KEY = stringPreferencesKey("password")

    suspend fun savePlanAttributes(context: Context, days: String, weeks: String) {
        context.planDataStore.edit { preferences ->
            preferences[DAYS_KEY] = days
            preferences[WEEKS_KEY] = weeks
        }
    }

    suspend fun getPlanDays(context: Context): String? {
        return context.planDataStore.data.map { preferences ->
            preferences[DAYS_KEY]
        }.first()
    }

    suspend fun getPlanWeeks(context: Context): String? {
        return context.planDataStore.data.map { preferences ->
            preferences[WEEKS_KEY]
        }.first()
    }

    suspend fun saveUserCredentials(context: Context, username: String, pass: String) {
        context.userDataStore.edit { preferences ->
            preferences[USER_KEY] = username
            preferences[PSS_KEY] = pass
        }
    }

    suspend fun getUserCredentials(context: Context): Pair<String?, String?> {
        return context.userDataStore.data.map { preferences ->
            val user = preferences[USER_KEY]
            val pass = preferences[PSS_KEY]
            Pair(user, pass)
        }.first()
    }
}
