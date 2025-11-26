package com.example.gymapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PlanConfirmActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_confirm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootConfirm)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvSummary = findViewById(R.id.tvSummary)

        val days = intent.getIntExtra(MainActivity.EXTRA_DAYS, 0)
        val weeks = intent.getIntExtra(MainActivity.EXTRA_WEEKS, 0)
        val sessions = days * weeks
        val summary = getString(R.string.summary_text, days, weeks, sessions)
        tvSummary.text = summary
    }
}
