package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.gymapp.R
import com.example.gymapp.helper.DataStoreHelper
import com.example.gymapp.helper.SQLiteHelper
import com.example.gymapp.model.Plan
import kotlinx.coroutines.launch

class CreatePlanActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etDays: EditText
    private lateinit var etWeeks: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_plan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val db = SQLiteHelper(this)
        etName = findViewById(R.id.etPlanName)
        etDays = findViewById(R.id.etDays)
        etWeeks = findViewById(R.id.etWeeks)

        lifecycleScope.launch {
            etDays.setText(DataStoreHelper.getPlanDays(this@CreatePlanActivity) ?: "")
            etWeeks.setText(DataStoreHelper.getPlanWeeks(this@CreatePlanActivity) ?: "")
        }

        val btnCreate: Button = findViewById(R.id.btnCreatePlan)

        btnCreate.setOnClickListener {
            if (!validateFields()) {
                return@setOnClickListener
            }
            val name = etName.text.toString().trim()
            val days = etDays.text?.toString()?.trim().orEmpty()
            val weeks = etWeeks.text?.toString()?.trim().orEmpty()

            lifecycleScope.launch {
                DataStoreHelper.savePlanAttributes(this@CreatePlanActivity, days, weeks)
            }

            val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                putExtra(EXTRA_NAME, name)
                putExtra(EXTRA_DAYS, days.toInt())
                putExtra(EXTRA_WEEKS, weeks.toInt())
            }
            startActivity(intent)
            finish()
        }

    }

    private fun validateFields(): Boolean {
        val nameStr = etName.text.toString().trim()
        val daysStr = etDays.text?.toString()?.trim().orEmpty()
        val weeksStr = etWeeks.text?.toString()?.trim().orEmpty()

        var valid = true

        if (nameStr.isEmpty()) {
            etName.error = getString(R.string.hint_plan_name)
            valid = false
        }

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
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DAYS = "extra_days"
        const val EXTRA_WEEKS = "extra_weeks"
        const val FROM_CLOSE_SESSION = "from_close_session"
    }
}