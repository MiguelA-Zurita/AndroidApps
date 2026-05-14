package com.example.cosmos.helper

import android.content.Context
import com.example.cosmos.enum.Endpoint
import com.example.cosmos.enum.Method
import com.example.cosmos.model.ConfigValues
import com.google.gson.Gson

class HttpRequester {

    fun sendHttpRequest(context: Context, method: Method, endpoint: Endpoint, headers: Map<String, String>, body: String? = null) {


        when(method){
            Method.GET -> {
                get(context, endpoint, headers)
            }
            Method.POST -> {
                post(context, endpoint, body.toString(), headers)
            }
            Method.PUT ->{

            }
        }
    }

    fun get(context: Context,endpoint: Endpoint, headers: Map<String, String>) {
        val jsonString = ConfigLoader().readJSONFromAssets(context, "assets/CosmosConfig.json")
        val jsondata = Gson().fromJson(jsonString, ConfigValues::class.java)
        var url = jsondata.cosmosServer.ip + ":" + jsondata.cosmosServer.port + "/game"
        when(endpoint) {
            Endpoint.STATUS -> {
              url += "/status/"
               GET(context, url, headers)
            }
            Endpoint.STATUS_ALL ->{
                url += "/all"
                GET(context, url, headers)
            }
            else -> {
                return
            }
        }
    }

    fun post(context: Context, endpoint: Endpoint, body: String, headers: Map<String, String>) {
        val jsonString = ConfigLoader().readJSONFromAssets(context, "assets/CosmosConfig.json")
        val jsondata = Gson().fromJson(jsonString, ConfigValues::class.java)
        var url = jsondata.cosmosServer.ip + ":" + jsondata.cosmosServer.port + "/game"
        when(endpoint) {
            Endpoint.JOIN -> {
                url += "/join"
                post(url, body, headers)
            }
            Endpoint.MOVE -> {
                url += "/move"
                post(url, body, headers)
            }
            else -> {
                return
            }
        }
    }

    fun post(url: String, data:String, headers: Map<String, String>) {

    }

    fun GET(context: Context, url: String, headers: Map<String, String>) {

    }

}