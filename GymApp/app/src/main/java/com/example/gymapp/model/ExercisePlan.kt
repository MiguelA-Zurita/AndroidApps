package com.example.gymapp.model

import java.sql.Date

data class ExercisePlan (
    val exerciseId: Long,
    val planId: Long,
    val repetitions: Int,
    val weight: Float,
    val seriesNo: Int,
    val date: Date,
    )