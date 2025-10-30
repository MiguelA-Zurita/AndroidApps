package com.example.lifecycle

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {

    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "creado")
        enableEdgeToEdge()
        setContentView(R.layout.main_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Button: open second screen
        findViewById<Button>(R.id.buttonOpenActivity).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        // Button: open URL in browser
        findViewById<Button>(R.id.buttonOpenWeb).setOnClickListener {
            val url = "https://www.cide.es".toUri()
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "empezado")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "resumido")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "pausado")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "detenido")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "reiniciado")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "destruido")
    }
}