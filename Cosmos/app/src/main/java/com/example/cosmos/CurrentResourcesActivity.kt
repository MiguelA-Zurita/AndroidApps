package com.example.cosmos

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CurrentResourcesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_resources)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            finish()
        }

        setupEditCrew(R.id.btn_edit_crew1, R.id.tv_crew1, "Capitan")
        setupEditCrew(R.id.btn_edit_crew2, R.id.tv_crew2, "Navegador")
        setupEditCrew(R.id.btn_edit_crew3, R.id.tv_crew3, "Ingeniero")
        setupEditCrew(R.id.btn_edit_crew4, R.id.tv_crew4, "Artillero")
    }

    private fun setupEditCrew(buttonId: Int, textViewId: Int, role: String) {
        findViewById<TextView>(buttonId).setOnClickListener {
            val textView = findViewById<TextView>(textViewId)
            showEditDialog(role, textView)
        }
    }

    private fun showEditDialog(role: String, textView: TextView) {
        val editText = EditText(this)
        val currentName = textView.text.toString().substringAfter(": ").trim()
        editText.setText(currentName)

        AlertDialog.Builder(this)
            .setTitle("Editar nombre de $role")
            .setView(editText)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = editText.text.toString()
                if (newName.isNotEmpty()) {
                    textView.text = "$role: $newName"
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
