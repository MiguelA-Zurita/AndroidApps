package com.example.gymapp.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.ExerciseListAdapter
import com.example.gymapp.controller.ControllerExerciseList
import com.example.gymapp.model.ExerciseList

class ExerciseList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exercise_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.exerciseList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvExerciseList = findViewById<RecyclerView>(R.id.rv_listactivity_exerciseslist)
        rvExerciseList.layoutManager = LinearLayoutManager(this)
        val rvController: ControllerExerciseList = ControllerExerciseList()
        val rvData: MutableList<ExerciseList> = rvController.getExerciseList(4)
        rvExerciseList.adapter = ExerciseListAdapter(rvData)
    }
}