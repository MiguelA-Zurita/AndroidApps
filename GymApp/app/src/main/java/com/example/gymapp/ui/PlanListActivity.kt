package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.ExerciseListAdapter
import com.example.gymapp.helper.NotificationHelper
import com.example.gymapp.helper.SQLiteHelper
import com.example.gymapp.model.ExerciseList
import com.example.gymapp.model.Plan
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlanListActivity : AppCompatActivity() {

    private lateinit var dbHelper: SQLiteHelper
    private lateinit var rvPlanList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = SQLiteHelper(this)
        rvPlanList = findViewById(R.id.rvPlanList)
        rvPlanList.layoutManager = LinearLayoutManager(this)

        val btnCreate: Button = findViewById(R.id.btnCreateNewPlan)
        btnCreate.setOnClickListener {
            val intent = Intent(this, CreatePlanActivity::class.java)
            startActivity(intent)
        }

        loadPlans()


    }

    private fun loadPlans() {
        val plans = dbHelper.getAll(Plan::class.java) as List<Plan>
        val rvData = plans.map { plan ->
            ExerciseList( //Utilizo el exerciseList por ahora, pero crearé otro data class para Planes especifico
                titulo = plan.name,
                descripcion = getString(R.string.plan_desc_format, plan.weeks, plan.daysPerWeek, if (plan.enabled) getString(R.string.plan_active_label) else ""),
                imagen = R.drawable.bulgara
            )
        }.toMutableList()

        rvPlanList.adapter = ExerciseListAdapter(rvData) { item ->
            val clickedPlan = plans.find { it.name == item.titulo }
            if (clickedPlan != null) {
                showPlanOptions(clickedPlan)
            }
        }
    }

    private fun showPlanOptions(plan: Plan) {
        val options = arrayOf(
            getString(R.string.option_activate_plan),
            getString(R.string.option_view_exercises),
            getString(R.string.option_delete)
        )
        AlertDialog.Builder(this)
            .setTitle(plan.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        dbHelper.setActivePlan(plan.id)
                        NotificationHelper.showPlanActivatedNotification(this, plan.name)
                        Toast.makeText(this, R.string.toast_plan_activated, Toast.LENGTH_SHORT).show()
                        
                        val intent = Intent(this, PlanConfirmActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) //Flags para marcar que el intent es un modal y que se vea de fondo el activity actual
                        startActivity(intent)
                        finish()
                    }
                    1 ->{
                        dbHelper.setActivePlan(plan.id)
                        NotificationHelper.showPlanActivatedNotification(this, plan.name)
                        val intent = Intent(this, PlanConfirmActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    2 -> {
                        dbHelper.delete(Plan::class.java, plan.id)
                        loadPlans()
                    }
                }
            }
            .show()
    }
}
