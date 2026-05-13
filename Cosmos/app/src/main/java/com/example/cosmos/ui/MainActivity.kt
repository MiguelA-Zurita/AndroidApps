package com.example.cosmos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cosmos.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_play).setOnClickListener {
            showStartGameDialog()
        }

        findViewById<Button>(R.id.btn_spectate).setOnClickListener {
            val intent = Intent(this, ViewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showStartGameDialog() {
        AlertDialog.Builder(this)
            .setMessage("¿Estás seguro que deseas empezar a jugar?")
            .setPositiveButton("Sí") { _, _ ->
                val intent = Intent(this, ChooseNickActivity::class.java)
                startActivity(intent)
            }
            .setNegativeButton("No", null)
            .show()
    }
}