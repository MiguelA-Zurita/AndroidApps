package com.example.gymapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.IntentCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.ExerciseListAdapter
import com.example.gymapp.helper.NotificationHelper
import com.example.gymapp.helper.SQLiteHelper
import com.example.gymapp.model.Exercise
import com.example.gymapp.model.ExerciseList
import com.example.gymapp.model.ExercisePlan
import com.example.gymapp.model.Plan
import com.example.gymapp.helper.DataStoreHelper
import com.example.gymapp.ui.CreatePlanActivity.Companion.FROM_CLOSE_SESSION
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import java.sql.Date


class PlanConfirmActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var dbHelper: SQLiteHelper
    private val pendingExercises = mutableMapOf<String, MutableList<TempExercise>>()
    private var lastClickedItem: ExerciseList? = null
    private lateinit var rvData: MutableList<ExerciseList>

    data class TempExercise(
        val name: String,
        val weight: Float,
        val reps: Int,
        val series: Int
    )

    private lateinit var addExerciseLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    private fun addExerciseActivity(item: ExerciseList) {
        lastClickedItem = item
        val intent = Intent(this, AddExerciseActivity::class.java)
        addExerciseLauncher.launch(intent)
    }

    private fun handleAddExerciseResult(result: androidx.activity.result.ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val data = result.data!!
            val exercisesList = IntentCompat.getParcelableArrayListExtra(
                data,
                AddExerciseActivity.EXTRA_EXERCISES_LIST,
                Bundle::class.java
            ) //Listado de info de ejercicios para describirlos en la pantalla para el usuario

            lastClickedItem?.let { item ->
                if (!exercisesList.isNullOrEmpty()) {
                    val list = pendingExercises.getOrPut(item.titulo) { mutableListOf() }

                    exercisesList.forEach { bundle ->
                        val name = bundle.getString(AddExerciseActivity.EXTRA_EXERCISE_NAME) ?: ""
                        val weight = bundle.getFloat(AddExerciseActivity.EXTRA_EXERCISE_WEIGHT, 0f)
                        val reps = bundle.getInt(AddExerciseActivity.EXTRA_EXERCISE_REPS, 0)
                        val series = bundle.getInt(AddExerciseActivity.EXTRA_EXERCISE_SERIES, 0)

                        if (name.isNotEmpty()) {
                            list.add(TempExercise(name, weight, reps, series))
                        }
                    }


                    val summary = list.joinToString(", ") { it.name } //Actualiza la description 
                    val index = rvData.indexOf(item)
                    if (index != -1) {
                        rvData[index] = item.copy(descripcion = summary)
                        findViewById<RecyclerView>(R.id.rvExerciseList).adapter?.notifyItemChanged(
                            index
                        ) //Se notifica al recyclerViewer que ha cambiado y debe actualizar la lista
                    }
                }
            }
        }
    }

    private val dayPrefix by lazy { getString(R.string.day_title, 0).replace("0", "").trim() }

    private fun savePlan(name: String, weeks: Int, days: Int) {
        val planId = dbHelper.insert(Plan(0, name, weeks, days, false))
        if (planId > 0) {
            val allExercises = dbHelper.getAll(Exercise::class.java) as List<Exercise>

            pendingExercises.forEach { (dayTitle, exercises) ->
                val dayIndex = dayTitle.replace(dayPrefix, "").trim().toIntOrNull() ?: 0
                val date = Date(dayIndex.toLong() * 86400000L)

                exercises.forEach { tempExercise ->
                    var exercise =
                        allExercises.find { it.name.equals(tempExercise.name, ignoreCase = true) }
                    var exId = exercise?.id ?: 0L
                    if (exId == 0L) {
                        exId = dbHelper.insert(Exercise(0, tempExercise.name))
                    }

                    dbHelper.insert(
                        ExercisePlan(
                            exId,
                            planId,
                            tempExercise.reps,
                            tempExercise.weight,
                            tempExercise.series,
                            date
                        )
                    )
                }
            }
            dbHelper.setActivePlan(planId)
            NotificationHelper.showPlanActivatedNotification(this, name)
            Toast.makeText(this, R.string.plan_saved, Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PlanConfirmActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, R.string.err_save_plan, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateText(planName: String, days: Int, weeks: Int) {
        val sessions = days * weeks
        val summary = getString(R.string.summary_new_plan, planName) + getString(
            R.string.summary_text,
            days,
            weeks,
            sessions
        )
        tvSummary.text = summary
        lifecycleScope.launch {
            DataStoreHelper.savePlanAttributes(
                this@PlanConfirmActivity,
                days.toString(),
                weeks.toString()
            )
        }

        val rvExerciseList = findViewById<RecyclerView>(R.id.rvExerciseList)
        rvExerciseList.layoutManager = LinearLayoutManager(this)
        rvData = mutableListOf()
        for (i in 1..days) {
            val dayTitle = getString(R.string.day_title, i)
            rvData.add(
                ExerciseList(
                    titulo = dayTitle,
                    descripcion = getString(R.string.add_exercise_hint),
                    imagen = R.drawable.ic_launcher_foreground
                )
            )
        }

        rvExerciseList.adapter = ExerciseListAdapter(rvData) { item ->
            addExerciseActivity(item)
        }

        val btnConfirm: Button = findViewById(R.id.btnConfirm)
        btnConfirm.visibility = android.view.View.VISIBLE
        btnConfirm.setOnClickListener {
            savePlan(planName, weeks, days)
        }
    }

    private fun loadActivePlan() {
        val activePlan = dbHelper.getActivePlan()
        if (activePlan != null) {
            val sessions = activePlan.daysPerWeek * activePlan.weeks
            val summary = getString(R.string.summary_active_plan, activePlan.name) +
                    getString(
                        R.string.summary_text,
                        activePlan.daysPerWeek,
                        activePlan.weeks,
                        sessions
                    )
            tvSummary.text = summary

            val rvExerciseList = findViewById<RecyclerView>(R.id.rvExerciseList)
            rvExerciseList.visibility = android.view.View.VISIBLE
            rvExerciseList.layoutManager = LinearLayoutManager(this)

            val exercisePlans = dbHelper.getExercisesForPlan(activePlan.id)
            val exercises = dbHelper.getAll(Exercise::class.java) as List<Exercise>

            rvData = mutableListOf()
            for (i in 1..activePlan.daysPerWeek) {
                val dayTitle = getString(R.string.day_title, i)
                val dayExercises = exercisePlans.filter {
                    val dayIndex =
                        it.date.time / 86400000L //Numero mágico, es 1 dia en milisegundos para calcular el dia
                    dayIndex == i.toLong()
                }

                val description = if (dayExercises.isNotEmpty()) {
                    dayExercises.joinToString(", ") { ep ->
                        val exName = exercises.find { it.id == ep.exerciseId }?.name
                            ?: getString(R.string.unknown_exercise)
                        getString(
                            R.string.exercise_desc_format,
                            exName,
                            ep.seriesNo,
                            ep.repetitions,
                            ep.weight
                        )
                    }
                } else {
                    getString(R.string.no_exercises)
                }

                rvData.add(
                    ExerciseList(
                        titulo = dayTitle,
                        descripcion = description,
                        imagen = R.drawable.ic_launcher_foreground
                    )
                )
            }
            rvExerciseList.adapter = ExerciseListAdapter(rvData)
        } else {
            tvSummary.text = getString(R.string.welcome_message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plan_confirm)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootConfirm)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Permís acceptat", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permís denegat", Toast.LENGTH_SHORT).show()
                }
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        dbHelper = SQLiteHelper(this)

        addExerciseLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                handleAddExerciseResult(result)
            }

        tvSummary = findViewById(R.id.tvSummary)
        val planName = intent.getStringExtra(CreatePlanActivity.EXTRA_NAME)
            ?: getString(R.string.plan_no_name)
        val days = intent.getIntExtra(CreatePlanActivity.EXTRA_DAYS, 0)
        val weeks = intent.getIntExtra(CreatePlanActivity.EXTRA_WEEKS, 0)

        if (days > 0 && weeks > 0) {
            updateText(planName, days, weeks)
        } else {
            loadActivePlan()
        }

        val tabLay = findViewById<TabLayout>(R.id.tlMenu);
        if (tabLay.tabCount == 0) {
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_list)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_registered_exercises)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_exercise_evolution)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_plan_statistics)))
        }

        tabLay.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val message = when (tab.position) {
                    0 -> getString(R.string.tl_list)
                    1 -> getString(R.string.tl_registered_exercises)
                    2 -> getString(R.string.tl_exercise_evolution)
                    3 -> getString(R.string.tl_plan_statistics)
                    else -> getString(R.string.err_generic)
                }
                Toast.makeText(this@PlanConfirmActivity, message, Toast.LENGTH_SHORT).show()
                if (tab.position == 1) {
                    val intent = Intent(
                        this@PlanConfirmActivity,
                        com.example.gymapp.ui.ExerciseList::class.java
                    )
                    startActivity(intent)
                }
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
        })

        val drawLay = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.navigation_view)
        val toolbar = findViewById<Toolbar>(R.id.mtlMenu)

        val toggle = ActionBarDrawerToggle(
            this, drawLay, toolbar,
            R.string.app_name, R.string.app_name
        )
        drawLay.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_view_plans -> {
                    val intent = Intent(this, PlanListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_activate_plan -> {
                    Toast.makeText(this, R.string.nav_activate_plan, Toast.LENGTH_SHORT).show()
                    true
                }

                R.id.nav_modify_exercise -> {
                    Toast.makeText(this, R.string.nav_modify_exercise, Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                R.id.nav_register_exercise -> {
                    Toast.makeText(this, R.string.nav_register_exercise, Toast.LENGTH_SHORT)
                        .show()
                    true
                }

                else -> false
            }.also {
                drawLay.closeDrawers()
            }
        }

        val fabWeekSelection: FloatingActionButton = findViewById(R.id.fab_week_selection)

        fabWeekSelection.setOnClickListener { view ->
            val popUp = PopupMenu(this, view)
            popUp.menuInflater.inflate(R.menu.week_menu, popUp.menu)
            popUp.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    /*R.id.fab_different_weeks -> {
                        Toast.makeText(this, R.string.fab_different_weeks, Toast.LENGTH_SHORT)
                            .show()
                        if (!validateFields()) {
                            false
                        }
                        val name = etName.text.toString().trim()
                        val days = etDays.text?.toString()?.trim().orEmpty().toIntOrNull()
                        val weeks = etWeeks.text?.toString()?.trim().orEmpty().toIntOrNull()

                        val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                            putExtra(EXTRA_NAME, name)
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
                        val name = etName.text.toString().trim()
                        val days = etDays.text?.toString()?.trim().orEmpty().toIntOrNull()
                        val weeks = etWeeks.text?.toString()?.trim().orEmpty().toIntOrNull()

                        val intent = Intent(this, PlanConfirmActivity::class.java).apply {
                            putExtra(EXTRA_NAME, name)
                            putExtra(EXTRA_DAYS, days)
                            putExtra(EXTRA_WEEKS, weeks)
                        }
                        startActivity(intent)
                        true
                    }*/
                    R.id.fab_close_session -> {
                        Toast.makeText(this, R.string.fab_close_session, Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra(FROM_CLOSE_SESSION, true)
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }
            popUp.show()
        }
    }

}