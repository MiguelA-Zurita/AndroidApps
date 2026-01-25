package com.example.gymapp.ui
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.datastore.core.DataStore
import androidx.lifecycle.lifecycleScope
import com.example.gymapp.R
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private val AppCompatActivity.dataStore by userCredentials(
        name = "user_credentials"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            etUsername = findViewById(R.id.et_login_username)
            etPassword = findViewById(R.id.et_login_password)
            val username = etUsername.text.toString()
            val userpass = etPassword.text.toString()
            if (validateFields(username, userpass)){
                lifecycleScope.launch {
                    dataStore.edit { preferences ->
                        preferences[userKey] = etKey1.text.toString()
                        preferences[pssKey] = etKey2.text.toString()
                    }
                }
            }
        }

    }

    fun validateFields(username: String, password: String): Boolean {

        return (username != "" &&
                username.isNotEmpty() &&
                password != "" &&
                password.isNotEmpty())
    }
}
