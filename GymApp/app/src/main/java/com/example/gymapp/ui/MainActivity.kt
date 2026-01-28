package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var etDays: EditText
    private lateinit var etWeeks: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etDays = findViewById(R.id.etDays)
        etWeeks = findViewById(R.id.etWeeks)

        val btnCreate: Button = findViewById(R.id.btnCreatePlan)
        val fabWeekSelection: FloatingActionButton = findViewById(R.id.fab_week_selection)

        btnCreate.setOnClickListener {
            if (!validateFields()) {
                return@setOnClickListener
            }
            val days = etDays.text?.toString()?.trim().orEmpty().toIntOrNull()
            val weeks = etWeeks.text?.toString()?.trim().orEmpty().toIntOrNull()

            val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                putExtra(EXTRA_DAYS, days)
                putExtra(EXTRA_WEEKS, weeks)
            }
            startActivity(intent)
        }

        fabWeekSelection.setOnClickListener { view ->
            val popUp = PopupMenu(this, view)
            popUp.menuInflater.inflate(R.menu.week_menu, popUp.menu)
            popUp.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.fab_different_weeks -> {
                        Toast.makeText(this, R.string.fab_different_weeks, Toast.LENGTH_SHORT)
                            .show()
                        if (!validateFields()) {
                            false
                        }
                        val days = etDays.text?.toString()?.trim().orEmpty().toIntOrNull()
                        val weeks = etWeeks.text?.toString()?.trim().orEmpty().toIntOrNull()

                        val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                            putExtra(EXTRA_DAYS, days)
                            putExtra(EXTRA_WEEKS, weeks)
                        }
                        startActivity(intent)
                        true
                    }

                    R.id.fab_equal_weeks -> {
                        Toast.makeText(this, R.string.fab_equal_weeks, Toast.LENGTH_SHORT).show()
                        if (!validateFields()) {
                            false
                        }
                        val days = etDays.text?.toString()?.trim().orEmpty().toIntOrNull()
                        val weeks = etWeeks.text?.toString()?.trim().orEmpty().toIntOrNull()

                        val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                            putExtra(EXTRA_DAYS, days)
                            putExtra(EXTRA_WEEKS, weeks)
                        }
                        startActivity(intent)
                        true
                    }
                    R.id.fab_close_session ->{
                        Toast.makeText(this, R.string.fab_close_session, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
            popUp.show()
        }
    }

    private fun validateFields(): Boolean {
        val daysStr = etDays.text?.toString()?.trim().orEmpty()
        val weeksStr = etWeeks.text?.toString()?.trim().orEmpty()

        var valid = true

        if (daysStr.isEmpty()) {
            etDays.error = getString(R.string.hint_days_per_week)
            valid = false
        }

        if (weeksStr.isEmpty()) {
            etWeeks.error = getString(R.string.hint_weeks)
            valid = false
        }

        val days = daysStr.toIntOrNull()
        val weeks = weeksStr.toIntOrNull()

        if (days == null || days !in 1..7) {
            etDays.error = getString(R.string.hint_days_per_week)
            valid = false
        }

        if (weeks == null || weeks !in 1..52) {
            etWeeks.error = getString(R.string.hint_weeks)
            valid = false
        }
        return valid

    }

    companion object {
        const val EXTRA_DAYS = "extra_days"
        const val EXTRA_WEEKS = "extra_weeks"
        const val EXTRA_SUMMARY = "extra_summary"
    }
}