package com.example.cosmos

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Paths

class ConfigLoader {

    private val json = Json { ignoreUnknownKeys = true }

    fun loadFromResource(resourceName: String = "/config.json"): AppConfig {
        val stream = javaClass.getResourceAsStream(resourceName)
            ?: throw IllegalStateException("No se ha encontrado el recurso $resourceName")

        val content = stream.bufferedReader().use { it.readText() }
        return json.decodeFromString(AppConfig.serializer(), content)
    }
}