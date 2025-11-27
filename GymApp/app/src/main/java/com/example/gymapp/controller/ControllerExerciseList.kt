package com.example.gymapp.controller

import com.example.gymapp.R
import com.example.gymapp.model.ExerciseList

class ControllerExerciseList {
    fun getExerciseList(len: Int): List<ExerciseList> {


     val listaEj: MutableList<ExerciseList> = emptyList<ExerciseList>().toMutableList();
     for (i in 1..len){
         val ejercicio = ExerciseList(
             Imagen = R.drawable.bulgara,
             Titulo = "Bulgara$i",
             Descripcion = "Haces una bulgara"
         )
         listaEj += ejercicio
     }
        return listaEj
    }
}