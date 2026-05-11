package com.example.cosmos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ChooseNickActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_nick)

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