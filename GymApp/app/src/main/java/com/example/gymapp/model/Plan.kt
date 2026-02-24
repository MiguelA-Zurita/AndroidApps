package com.example.gymapp.model

data class Plan(
    val id: Long, val name: String, val weeks: Int, val daysPerWeek: Int, val enabled: Boolean
) {
    companion object {
        const val TABLE_NAME = "plans"
        const val ID_COLUMN = "id"
        const val WEEKS_COLUMN = "weeks"
        const val DAYS_PER_WEEK_COLUMN = "daysPerWeek"
        const val ENABLED_COLUMN = "enabled"
        const val NAME_COLUMN = "name"
    }
}
