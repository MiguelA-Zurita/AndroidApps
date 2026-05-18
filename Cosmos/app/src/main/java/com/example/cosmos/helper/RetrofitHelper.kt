package com.example.cosmos.helper

import android.content.Context
import com.example.cosmos.interfaces.CosmosApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    @Volatile
    private var apiService: CosmosApiService? = null

    fun getApi(context: Context): CosmosApiService {
        return apiService ?: synchronized(this) {
            apiService ?: createApi(context).also { apiService = it }
        }
    }

    private fun createApi(context: Context): CosmosApiService {
        val config = ConfigManager.loadConfig(context)
        val baseUrl = "http://${config.cosmosServer.host}:${config.cosmosServer.port}/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CosmosApiService::class.java)
    }
}