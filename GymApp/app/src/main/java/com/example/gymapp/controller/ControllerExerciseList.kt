package com.example.gymapp.controller

import com.example.gymapp.R
import com.example.gymapp.model.ExerciseList

class ControllerExerciseList {
    fun getExerciseList(len: Int): List<ExerciseList> {


     var listaEj: List<ExerciseList> = emptyList()
     for (i in 1..len){
         val ejercicio = ExerciseList(
             Imagen = R.drawable.bulgara,
             Titulo = "Bulgara$i",
             Descripcion = "Haces una bulgara"
         )
         listaEj += ejercicio
     }
        return listaEj
        //listOf(
        //ExerciseList(
        //    Imagen = R.drawable.bulgara,
        //    Titulo = "Bulgara",
        //    Descripcion = "haces una bulgara"
        //))
    }
}