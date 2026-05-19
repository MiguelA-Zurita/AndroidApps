package com.example.cosmos.helper

import android.content.Context
import com.example.cosmos.model.MyStatusRequestDto
import com.example.cosmos.model.MyStatusResponseDto
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class MyStatusRequester(){
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getMyStatus(context: Context, idNau: Int): Result<MyStatusResponseDto> = withContext(Dispatchers.IO) {

        val config = ConfigManager.loadConfig(context)
        val baseUrl = "http://${config.cosmosServer.host}:${config.cosmosServer.port}/"

        runCatching {
            val json = gson.toJson(MyStatusRequestDto(idNau))
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("${baseUrl}game/mystatus")
                .method("GET", body)
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("HTTP ${response.code}")
                }

                val responseBody = response.body?.string()
                    ?: throw Exception("Respuesta vacía")

                gson.fromJson(responseBody, MyStatusResponseDto::class.java)
            }
        }
    }
}