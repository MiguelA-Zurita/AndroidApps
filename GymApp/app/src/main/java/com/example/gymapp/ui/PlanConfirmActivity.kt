package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gymapp.R
import com.example.gymapp.adapter.ExerciseListAdapter
import com.example.gymapp.model.ExerciseList
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch


class PlanConfirmActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView

    private val AppCompatActivity.dataStore by preferencesDataStore(
        name = "plan_attributes"
    )

    private val daysKey = stringPreferencesKey("days")
    private val weeksKey = stringPreferencesKey("weeks")

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
        lifecycleScope.launch {
            dataStore.edit { preferences ->
                preferences[daysKey] = days.toString()
                preferences[weeksKey] = weeks.toString()
            }
        }


        val btnConfirm: Button = findViewById(R.id.btnConfirm)
        btnConfirm.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra(MainActivity.EXTRA_SUMMARY, summary)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        val rvExerciseList = findViewById<RecyclerView>(R.id.rvExerciseList)
        rvExerciseList.layoutManager = LinearLayoutManager(this)
        val rvData: MutableList<ExerciseList> = emptyList<ExerciseList>().toMutableList()
            for (i in 1..days){
                val ejercicio = ExerciseList(
                    titulo = "dia $i",
                    descripcion = " ",
                    imagen = R.drawable.ic_launcher_foreground
                )
                rvData += ejercicio
            }

            rvExerciseList.adapter = ExerciseListAdapter(rvData)

        val tabLay = findViewById<TabLayout>(R.id.tlMenu);
        if (tabLay.tabCount == 0){
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_list)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_registered_exercises)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_exercise_evolution)))
            tabLay.addTab(tabLay.newTab().setText(getString(R.string.tl_plan_statistics)))
        }

        tabLay.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                val message = when(tab.position){
                    0 -> getString(R.string.tl_list)
                    1 -> getString(R.string.tl_registered_exercises)
                    2 -> getString(R.string.tl_exercise_evolution)
                    3 -> getString(R.string.tl_plan_statistics)
                    else -> "Si ves esto, algo he hecho mal"
                }
                Toast.makeText(this@PlanConfirmActivity, message, Toast.LENGTH_SHORT).show()
                if (tab.position == 1){
                    val intent = Intent(this@PlanConfirmActivity, ExerciseList::class.java)
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
            when(item.itemId){
                R.id.nav_activate_plan ->{
                    Toast.makeText(this, R.string.nav_activate_plan, Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_modify_exercise ->{
                    Toast.makeText(this, R.string.nav_modify_exercise, Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_register_exercise ->{
                    Toast.makeText(this, R.string.nav_register_exercise, Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }.also{
                drawLay.closeDrawers()
            }
        }
    }
}