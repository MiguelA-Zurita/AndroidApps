package com.example.cosmos.ui

import android.os.Bundle
import android.util.Log
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
import com.example.cosmos.helper.DataStoreHelper
import com.example.cosmos.helper.DatabaseProvider
import com.example.cosmos.helper.RetrofitHelper
import com.example.cosmos.model.Tripulante
import com.example.cosmos.repository.CosmosRepository
import kotlinx.coroutines.launch

class CurrentResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_resources)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        updateResources()
    }

    private fun updateResources() {
        val rvCrewMembers = findViewById<RecyclerView>(R.id.rv_crew_members)
        rvCrewMembers.layoutManager = LinearLayoutManager(this)

        val db = DatabaseProvider.create(this)

        lifecycleScope.launch {
            try {
                val crewMembers = db.getCrewMembersByShip(
                    DataStoreHelper.getShipID(this@CurrentResourcesActivity).toInt()
                )

                val adapter = CrewAdapter(crewMembers) { position ->
                    showEditDialog(crewMembers.toMutableList(), position)
                }
                rvCrewMembers.adapter = adapter

                val tvFoodQuantity = findViewById<TextView>(R.id.tv_current_food)
                val tvWeaponQuantity = findViewById<TextView>(R.id.tv_current_weapons)

                val shipStatus =
                    db.getShipById(DataStoreHelper.getShipID(this@CurrentResourcesActivity).toInt())

                val food = shipStatus?.quantitatAliments
                val weapons = shipStatus?.quantitatArmes

                tvWeaponQuantity.text = getString(R.string.weapons_format, weapons)
                tvFoodQuantity.text = getString(R.string.food_format, food)
            } catch (e: Exception) {
                Log.d("CurrentResourcesActivity", "Error fetching data: ${e.message}")
            }
        }

    }

    private fun showEditDialog(crewMembers: MutableList<Tripulante>, position: Int) {
        val editText = EditText(this)
        val currentEntry = crewMembers[position].nom
        editText.setText(currentEntry)

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_member_title, currentEntry))
            .setView(editText)
            .setPositiveButton(R.string.save) { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotEmpty()) {
                    crewMembers[position].nom = newName
                    findViewById<RecyclerView>(R.id.rv_crew_members).adapter?.notifyItemChanged(
                        position
                    )
                    syncUpdate(crewMembers[position].idTripulant, newName)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun syncUpdate(crewId: Int, newName: String){
        val api = RetrofitHelper().getApi(this)
        val repository = CosmosRepository(api)

        lifecycleScope.launch {
            try {
                repository.renameCrewMember(DataStoreHelper.getShipID(this@CurrentResourcesActivity).toInt(), crewId, newName)

                val db = DatabaseProvider.create(this@CurrentResourcesActivity)

                val crewMate = db.getCrewMember(crewId)

                crewMate?.nom = newName

                db.updateCrewMember(crewMate)
            } catch (e: Exception) {
                Log.d("CurrentResourcesActivity", "Error updating crew member: ${e.message}")
            }
        }
    }
}