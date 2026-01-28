package com.example.gymapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.gymapp.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private val AppCompatActivity.dataStore by preferencesDataStore(
        name = "user_credentials"
    )
    private val userKey = stringPreferencesKey("username")
    private val pssKey = stringPreferencesKey("password")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            etUsername = findViewById(R.id.et_login_username)
            etPassword = findViewById(R.id.et_login_password)
            val username = etUsername.text.toString()
            val userpass = etPassword.text.toString()
            if (validateFields(username, userpass)) {
                lifecycleScope.launch {
                    val preferences = dataStore.data.first()
                    if (username == preferences[userKey] && userpass == preferences[pssKey]) {
                        Toast.makeText(this@LoginActivity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                        launchMain()
                    } else{
                        Toast.makeText(this@LoginActivity, "Usernme or password incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            } else Toast.makeText(this@LoginActivity, "Username or password is blank", Toast.LENGTH_SHORT).show()
        }

        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            etUsername = findViewById(R.id.et_login_username)
            etPassword = findViewById(R.id.et_login_password)
            val username = etUsername.text.toString()
            val userpass = etPassword.text.toString()
            if (validateFields(username, userpass)) {
                lifecycleScope.launch {
                    dataStore.edit { preferences ->
                        preferences[userKey] = username
                        preferences[pssKey] = userpass
                    }
                }
                launchMain()
            }
        }
    }

    fun validateFields(username: String, password: String): Boolean {
        return (!username.isBlank() &&
                !password.isBlank())
    }

    fun launchMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
