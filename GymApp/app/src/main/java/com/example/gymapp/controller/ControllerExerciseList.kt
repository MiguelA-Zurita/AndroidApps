package com.example.gymapp.controller

import com.example.gymapp.R
import com.example.gymapp.model.ExerciseList

class ControllerExerciseList {

    public fun getExerciseList(len: Int): MutableList<ExerciseList> {
     val listaEj: MutableList<ExerciseList> = emptyList<ExerciseList>().toMutableList();
     for (i in 1..len){
         val ejercicio = ExerciseList(
             imagen = R.drawable.bulgara,
             titulo = "Bulgara$i",
             descripcion = "Haces $i bulgara/s"
         )
         listaEj += ejercicio
     }
        return listaEj
    }
}