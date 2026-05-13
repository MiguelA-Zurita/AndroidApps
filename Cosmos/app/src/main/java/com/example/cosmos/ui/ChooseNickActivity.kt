package com.example.cosmos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmos.R
import com.example.cosmos.helper.ConfigLoader
import com.example.cosmos.model.ConfigValues
import com.google.gson.Gson

class ChooseNickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nick)

        val jsonString = ConfigLoader().readJSONFromAssets(this, "assets/CosmosConfig.json")
        val data = Gson().fromJson(jsonString, ConfigValues::class.java)

        findViewById<Button>(R.id.btn_accept).setOnClickListener {
            val nick = findViewById<EditText>(R.id.et_nickname).text.toString()
            if (nick.isNotEmpty()) {
                showTeamAssignedDialog()
            }
        }
    }

    private fun showTeamAssignedDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_team_assigned, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<Button>(R.id.btn_perfecto).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, AssignedResourcesActivity::class.java)
            startActivity(intent)
        }

        dialog.show()
    }
}