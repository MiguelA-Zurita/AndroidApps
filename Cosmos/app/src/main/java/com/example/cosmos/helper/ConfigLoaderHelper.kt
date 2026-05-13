package com.example.cosmos.helper

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.readJson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Paths

class ConfigLoader {

    fun ReadJSONFromAssets(context: Context, path: String): String {
        val identifier = "[ReadJSON]"
        try {
            val file = context.assets.open(path)
            Log.i(
                identifier,
                "Found File: $file.",
            )
            val bufferedReader = BufferedReader(InputStreamReader(file))
            val stringBuilder = StringBuilder()
            bufferedReader.useLines { lines ->
                lines.forEach {
                    stringBuilder.append(it)
                }
            }
            Log.i(
                identifier,
                "getJSON stringBuilder: $stringBuilder.",
            )
            val jsonString = stringBuilder.toString()
            Log.i(
                identifier,
                "JSON as String: $jsonString.",
            )
            return jsonString
        } catch (e: Exception) {
            Log.e(
                identifier,
                "Error reading JSON: $e.",
            )
            e.printStackTrace()
            return ""
        }
    }
}
