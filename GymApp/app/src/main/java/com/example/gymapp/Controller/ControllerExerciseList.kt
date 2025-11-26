package com.example.gymapp.Controller

import com.example.gymapp.R
import com.example.gymapp.model.ExerciseList

class ControllerExerciseList {
    fun getExerciseList(): List<ExerciseList> = listOf(
        ExerciseList(
            Imagen = R.drawable.bulgara,
            Titulo = "Bulgara",
            Descripcion = "haces una bulgara"
        ),
        ExerciseList(
            Imagen = R.drawable.bulgara,
            Titulo = "Bulgara2",
            Descripcion = "haces dos bulgaras"
        )

    )
}