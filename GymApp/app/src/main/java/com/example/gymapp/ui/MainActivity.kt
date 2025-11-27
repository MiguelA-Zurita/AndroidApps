package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.gymapp.ui.PlanConfirmActivity
import com.example.gymapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var etDays: EditText
    private lateinit var etWeeks: EditText
    private lateinit var tvResult: TextView

    private val confirmLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val summary = result.data?.getStringExtra(EXTRA_SUMMARY).orEmpty()
            tvResult.text = summary
        } else {
            tvResult.text = getString(R.string.operation_canceled)
        }
    }

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
        tvResult = findViewById(R.id.tvResult)

        val btnCreate: Button = findViewById(R.id.btnCreatePlan)
        val fabWeekSelection: FloatingActionButton = findViewById<FloatingActionButton>(R.id.fab_week_selection);

        btnCreate.setOnClickListener {
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
            if (!valid) return@setOnClickListener

            val days = daysStr.toIntOrNull()
            val weeks = weeksStr.toIntOrNull()

            if (days == null || days !in 1..7) {
                etDays.error = getString(R.string.hint_days_per_week)
                return@setOnClickListener
            }
            if (weeks == null || weeks !in 1..52) {
                etWeeks.error = getString(R.string.hint_weeks)
                return@setOnClickListener
            }

            val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                putExtra(EXTRA_DAYS, days)
                putExtra(EXTRA_WEEKS, weeks)
            }
            confirmLauncher.launch(intent)
        }
        fabWeekSelection.setOnClickListener { view ->
            val popUp = PopupMenu(this, view);
            //popUp.menuInflater.inflate()
        }
    }

    companion object {
        const val EXTRA_DAYS = "extra_days"
        const val EXTRA_WEEKS = "extra_weeks"
        const val EXTRA_SUMMARY = "extra_summary"
    }
}