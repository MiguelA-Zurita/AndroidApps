package com.example.cosmos.model


data class CosmosConfig(
    val cosmosServer: CosmosServerConfig,
    val chatMongoDB: ChatMongoConfig,
    val localPersistence: LocalPersistenceConfig,
    val playerSettings: PlayerSettingsConfig
)

data class CosmosServerConfig(
    val host: String,
    val port: Int
)

data class ChatMongoConfig(
    val uri: String,
    val collection: String
)

data class LocalPersistenceConfig(
    val driver: String,
    val path: String
)

data class PlayerSettingsConfig(
    var nauID: Int
)
