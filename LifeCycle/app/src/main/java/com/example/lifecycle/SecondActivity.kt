package com.example.lifecycle

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private val TAG = this::class.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Creado")
        enableEdgeToEdge()
        setContentView(R.layout.segunda_activity)
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
        Log.d(TAG, "Detenido")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "reiniciado")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Destruido")
    }
}
