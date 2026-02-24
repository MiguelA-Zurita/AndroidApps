package com.example.gymapp.model

import java.sql.Date

data class ExercisePlan (
    val exerciseId: Long,
    val planId: Long,
    val repetitions: Int,
    val weight: Float,
    val seriesNo: Int,
    val date: Date,
    ){
    companion object{
        const val TABLE_NAME = "exercise_plan"
        const val EXERCISE_ID_COLUMN = "idExercise"
        const val PLAN_ID_COLUMN = "idPlan"
        const val DATE_COLUMN = "exerciseDate"
        const val REPETITIONS_COLUMN = "repetitions"
        const val WEIGHT_COLUMN = "weight"
        const val SERIES_COLUMN = "seriesNo"
    }
}