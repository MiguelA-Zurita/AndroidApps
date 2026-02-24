package com.example.gymapp.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.R
import com.example.gymapp.helper.SQLiteHelper
import com.example.gymapp.model.Exercise

class AddExerciseActivity : AppCompatActivity() {

    private lateinit var dbHelper: SQLiteHelper
    private val addedExercises = ArrayList<Bundle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_exercise)
        val rootView = findViewById<android.view.View>(android.R.id.content)
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = SQLiteHelper(this)

        val etName = findViewById<AutoCompleteTextView>(R.id.etExerciseName)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etReps = findViewById<EditText>(R.id.etRepetitions)
        val etSeries = findViewById<EditText>(R.id.etSeries)
        val btnAdd = findViewById<Button>(R.id.btnAddExercise)
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val weight = etWeight.text.toString().toFloatOrNull() ?: 0f
            val reps = etReps.text.toString().toIntOrNull() ?: 0
            val series = etSeries.text.toString().toIntOrNull() ?: 0

            if (name.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString(EXTRA_EXERCISE_NAME, name)
                    putFloat(EXTRA_EXERCISE_WEIGHT, weight)
                    putInt(EXTRA_EXERCISE_REPS, reps)
                    putInt(EXTRA_EXERCISE_SERIES, series)
                }
                addedExercises.add(bundle)

                etName.setText("")
                etWeight.setText("")
                etReps.setText("")
                etSeries.setText("")
                etName.requestFocus()
                
                Toast.makeText(this, R.string.exercise_added, Toast.LENGTH_SHORT).show()
            } else {
                etName.error = getString(R.string.err_name_required)
            }
        }

        btnFinish.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                val bundle = Bundle().apply {
                    putString(EXTRA_EXERCISE_NAME, name)
                    putFloat(EXTRA_EXERCISE_WEIGHT, etWeight.text.toString().toFloatOrNull() ?: 0f)
                    putInt(EXTRA_EXERCISE_REPS, etReps.text.toString().toIntOrNull() ?: 0)
                    putInt(EXTRA_EXERCISE_SERIES, etSeries.text.toString().toIntOrNull() ?: 0)
                }
                addedExercises.add(bundle)
            }

            if (addedExercises.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putParcelableArrayListExtra(EXTRA_EXERCISES_LIST, addedExercises)
                setResult(RESULT_OK, resultIntent)
            }
            finish()
        }
    }

    companion object {
        const val EXTRA_EXERCISES_LIST = "extra_exercises_list"
        const val EXTRA_EXERCISE_NAME = "extra_exercise_name"
        const val EXTRA_EXERCISE_WEIGHT = "extra_exercise_weight"
        const val EXTRA_EXERCISE_REPS = "extra_exercise_reps"
        const val EXTRA_EXERCISE_SERIES = "extra_exercise_series"
    }
}
