package com.example.cosmos.helper

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.playerDataStore: DataStore<Preferences> by preferencesDataStore(name = "player_config")

object DataStoreHelper {

    private val SHIP_KEY = stringPreferencesKey("ship_id")
    private val TEAM_KEY = stringPreferencesKey("team_color")

    suspend fun saveShipID(context: Context, shipID: Int) {
        context.playerDataStore.edit { preferences ->
            preferences[SHIP_KEY] = shipID.toString()
        }
    }

    suspend fun getShipID(context: Context): String {
        return context.playerDataStore.data.map { preferences ->
            preferences[SHIP_KEY] ?: "0"
        }.first()
    }

    suspend fun saveTeamColor(context: Context, color: String) {
        context.playerDataStore.edit { preferences ->
            preferences[TEAM_KEY] = color
        }
    }

    suspend fun getTeamColor(context: Context): String {
        return context.playerDataStore.data.map { preferences ->
            preferences[TEAM_KEY] ?: "0"
        }.first()
    }
}
