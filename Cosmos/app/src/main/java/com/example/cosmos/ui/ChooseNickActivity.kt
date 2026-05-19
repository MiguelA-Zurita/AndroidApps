package com.example.cosmos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.cosmos.R
import com.example.cosmos.helper.ConfigManager
import com.example.cosmos.helper.DataStoreHelper
import com.example.cosmos.helper.DatabaseProvider
import com.example.cosmos.helper.RetrofitHelper
import com.example.cosmos.model.JoinGameResponseDto
import com.example.cosmos.model.Nave
import com.example.cosmos.model.Tripulante
import com.example.cosmos.repository.CosmosRepository
import kotlinx.coroutines.launch

class ChooseNickActivity : AppCompatActivity() {
    private lateinit var repository: CosmosRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nick)

        val db = DatabaseProvider.create(this)

        lifecycleScope.launch {
            db.clearAllData()
            db.close()
        }

        val api = RetrofitHelper().getApi(this)

        repository = CosmosRepository(api)

        val btnAccept = findViewById<Button>(R.id.btn_accept)

        btnAccept.setOnClickListener {
            val nick = findViewById<EditText>(R.id.et_nickname).text.toString()
            if (nick.isNotEmpty()) {
                showTeamAssignedDialog(nick)
            }
        }
    }

    private fun showTeamAssignedDialog(nick: String) {
        val btnAccept = findViewById<Button>(R.id.btn_accept)

        val dialogView = layoutInflater.inflate(R.layout.dialog_team_assigned, null)
        val tvAssignedTeam = dialogView.findViewById<TextView>(R.id.tv_assigned_team)

        val intent = Intent(this, AssignedResourcesActivity::class.java)

        lifecycleScope.launch {
            try {
                btnAccept.isEnabled = false

                val response = repository.joinGame(nick)

                syncDatabase(response)

                tvAssignedTeam.text = response.equip
                tvAssignedTeam.setBackgroundColor(getColor(getTeamColor(response.equip)))

            } catch (e: Exception) {
                Toast.makeText(
                    this@ChooseNickActivity,
                    getString(R.string.error_message_format, e.message),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                btnAccept.isEnabled = true
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btn_perfecto).setOnClickListener {
            dialog.dismiss()
            startActivity(intent)
        }

        dialog.show()
    }


    fun syncDatabase(data: JoinGameResponseDto) {
        val db = DatabaseProvider.create(this)

        val nave = Nave(
            idNau = data.id_nau,
            quantitatAliments = data.recursos.quantitat_aliments,
            quantitatArmes = data.recursos.quantitat_armes,
            posX = data.posicio_inicial.x,
            posY = data.posicio_inicial.y
        )
        db.updateShip(nave)

        lifecycleScope.launch {
            DataStoreHelper.saveShipID(this@ChooseNickActivity, data.id_nau)
            DataStoreHelper.saveTeamColor(this@ChooseNickActivity, data.equip)
        }


        data.recursos.tripulacio.forEach {
            val tripulante = Tripulante(
                idTripulant = it.id_tripulant,
                idNau = data.id_nau,
                nom = it.nom,
                estatVital = it.estat_vital
            )
            db.updateCrewMember(tripulante)
        }
    }

    fun getTeamColor(team: String): Int {
        return when (team) {
            "rojo" -> R.color.teamcolor_red
            "azul" -> R.color.teamcolor_blue
            "verde" -> R.color.teamcolor_green
            "amarillo" -> R.color.yellow
            else -> R.color.black
        }
    }

}
