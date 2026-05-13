package com.example.cosmos.model


data class ConfigValues(
    val cosmosServer: CosmosServer,
    val chatDB: ChatDB,
    val localDB: LocalDB,
    val playerSettings: PlayerSettings
) {
    data class CosmosServer(
        val ip: String,
        val port: Int)

    data class ChatDB(
        val uri: String,
        val collection: String
    )

    data class LocalDB(
        val driver: String,
        val path: String
    )

    data class PlayerSettings(
        val id: Int
    )
}
