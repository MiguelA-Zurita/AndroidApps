package com.example.cosmos.model

data class Nave(
    val idNau: Int,
    val quantitatAliments: Int,
    val quantitatArmes: Int,
    val posX: Int,
    val posY: Int
)

data class Tripulante(
    val idTripulant: Int,
    val idNau: Int,
    var nom: String,
    val estatVital: Boolean
)

data class EstadoTablero(
    val casellaId: Int,
    val nauID: Int,
    val planeta: Boolean,
    val x: Int,
    val y: Int,
)