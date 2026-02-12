package com.example.gymapp.model

data class Plan(
    val id: Long, val name: String, val weeks: Int
) {
    companion object {
        const val ID_COLUMN = "id"
        const val NAME_COLUMN = "name"
    }
}
