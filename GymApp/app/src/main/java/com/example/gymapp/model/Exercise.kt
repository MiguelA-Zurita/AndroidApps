package com.example.gymapp.model

data class Exercise(
    val id: Long,
    val name: String,
) {
    companion object {
        const val ID_COLUMN = "id";
        const val NAME_COLUMN = "name";
    }
}
