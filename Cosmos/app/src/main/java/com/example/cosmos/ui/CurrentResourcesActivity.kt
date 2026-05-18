package com.example.cosmos.ui

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.R
import com.example.cosmos.adapter.CrewAdapter
import com.example.cosmos.helper.RetrofitHelper
import com.example.cosmos.repository.CosmosRepository
import kotlinx.coroutines.launch

class CurrentResourcesActivity : AppCompatActivity() {
    private lateinit var repository: CosmosRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_resources)

        val api = RetrofitHelper().getApi(this)

        repository = CosmosRepository(api)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }
        updateResources()

    }

    private fun updateResources() {
        val rvCrewMembers = findViewById<RecyclerView>(R.id.rv_crew_members)
        rvCrewMembers.layoutManager = LinearLayoutManager(this)

        /*val db = DatabaseProvider.create(this)

        val crewMembers = db.getTripulantsByNau(intent.getIntExtra("nauID", -1)
        ).map {
            CrewMemberDto(
                id_tripulant = it.idTripulant,
                nom = it.nom,
                estat_vital = it.estatVital
            )
        }.toMutableList()*/

        lifecycleScope.launch {
            //val myStatusData = repository.getMyStatus(1) // da error debido a que el GET tiene body, y el retrofit no lo permite?

            val crewMembers = generateDummyData()

            val adapter = CrewAdapter(crewMembers) { position ->
                showEditDialog(crewMembers, position)
            }
            rvCrewMembers.adapter = adapter

            val tvFoodQuantity = findViewById<TextView>(R.id.tv_current_food)
            val tvWeaponQuantity = findViewById<TextView>(R.id.tv_current_weapons)

            val food = 10
            val weapons = 4

            tvWeaponQuantity.text = getString(R.string.weapons_format, weapons)
            tvFoodQuantity.text = getString(R.string.food_format, food)
        }

    }

    private fun generateDummyData(): MutableList<String?> {
        return mutableListOf(
            getString(R.string.crew_member_1),
            getString(R.string.crew_member_2),
            getString(R.string.crew_member_3)
        )
    }


    private fun showEditDialog(crewMembers: MutableList<String?>, position: Int) {
        val editText = EditText(this)
        val currentEntry = crewMembers[position]
        editText.setText(currentEntry)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_member_title, currentEntry))
            .setView(editText)
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotEmpty()) {
                    crewMembers[position] = newName
                    findViewById<RecyclerView>(R.id.rv_crew_members).adapter?.notifyItemChanged(
                        position
                    )
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}