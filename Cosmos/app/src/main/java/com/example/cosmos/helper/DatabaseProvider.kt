package com.example.cosmos.helper

import android.content.Context

object DatabaseProvider {

    fun create(context: Context): SQLiteHelper {
        val config = ConfigManager.loadConfig(context)

        require(config.localPersistence.driver.lowercase() == "sqlite") {
            "Driver no soportado: ${config.localPersistence.driver}"
        }

        val dbName = config.localPersistence.path.substringAfterLast("/")

        return SQLiteHelper(context, dbName)
    }
}