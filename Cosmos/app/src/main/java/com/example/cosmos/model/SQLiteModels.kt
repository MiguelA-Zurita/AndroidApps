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
    val nom: String,
    val estatVital: Boolean
)

data class EstadoTablero(
    val idNau: Int,
    val planeta: Boolean,
    val planetas: List<Planetas>,
)

data class Planetas(
    val cellId: Int,
    val x: Int,
    val y: Int
)