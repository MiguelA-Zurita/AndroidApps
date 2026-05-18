package com.example.cosmos.helper

import android.content.Context
import com.example.cosmos.model.CosmosConfig

import com.google.gson.Gson

object ConfigManager {

    private const val CONFIG_FILE_NAME = "CosmosConfig.json"

    fun loadConfig(context: Context): CosmosConfig {
        val json = context.assets
            .open(CONFIG_FILE_NAME)
            .bufferedReader()
            .use { it.readText() }

        return Gson().fromJson(json, CosmosConfig::class.java)
    }

    fun saveConfig(context: Context, config: CosmosConfig) {
        val json = Gson().toJson(config)
        context.openFileOutput(CONFIG_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(json.toByteArray())
        }
    }
}
