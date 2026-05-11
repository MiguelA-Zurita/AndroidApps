package com.example.cosmos

data class BoardCell(
    val teamOwner: Int = 0, // 0: none, 1: blue, 2: red, 3: pink, 4: green
    val hasPlanet: Boolean = false,
    val ships: IntArray = intArrayOf(0, 0, 0, 0) // counts for each team
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as BoardCell
        if (teamOwner != other.teamOwner) return false
        if (hasPlanet != other.hasPlanet) return false
        if (!ships.contentEquals(other.ships)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = teamOwner
        result = 31 * result + hasPlanet.hashCode()
        result = 31 * result + ships.contentHashCode()
        return result
    }
}