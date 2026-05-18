package com.example.cosmos.model

data class BoardCell(
    val teamOwner: Int = 0, // 0: nada, 1: azul, 2: rojo, 3: verde
    val hasPlanet: Boolean = false,
    val ships: IntArray = intArrayOf(0, 0, 0, 0) // contador de naves
) {
    override fun equals(other: Any?): Boolean { //Creado por el IDE (no se que hace)
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BoardCell
        if (teamOwner != other.teamOwner) return false
        if (hasPlanet != other.hasPlanet) return false
        if (!ships.contentEquals(other.ships)) return false
        return true
    }

    override fun hashCode(): Int { //Creado por el IDE (no se que hace)
        var result = teamOwner
        result = 31 * result + hasPlanet.hashCode()
        result = 31 * result + ships.contentHashCode()
        return result
    }
}