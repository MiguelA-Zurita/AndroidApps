package com.example.cosmos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cosmos.R
import com.example.cosmos.adapter.ReadOnlyCrewAdapter
import com.example.cosmos.helper.DataStoreHelper
import com.example.cosmos.helper.DatabaseProvider
import kotlinx.coroutines.launch

class AssignedResourcesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assigned_resources)

        lifecycleScope.launch {
            updateList()
        }

        findViewById<Button>(R.id.btn_comenzar).setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

    suspend fun updateList() {
        val db = DatabaseProvider.create(this)
        val shipID = DataStoreHelper.getShipID(this@AssignedResourcesActivity).toInt()

        val ship = db.getShipById(shipID)

        val rvCrewMembers = findViewById<RecyclerView>(R.id.rv_crew_members)
        rvCrewMembers.layoutManager = LinearLayoutManager(this)
        rvCrewMembers.adapter = ReadOnlyCrewAdapter(db.getCrewMembersByShip(shipID))

        val tvFoodQuantity = findViewById<TextView>(R.id.tv_food)
        val tvWeaponQuantity = findViewById<TextView>(R.id.tv_weapons)

        tvFoodQuantity.text = getString(R.string.food_format, ship?.quantitatAliments)
        tvWeaponQuantity.text = getString(R.string.weapons_format, ship?.quantitatArmes)
    }
}