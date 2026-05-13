package com.example.cosmos.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cosmos.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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