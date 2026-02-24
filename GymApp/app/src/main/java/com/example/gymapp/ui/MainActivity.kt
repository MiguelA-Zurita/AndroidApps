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
import androidx.lifecycle.lifecycleScope
import com.example.gymapp.R
import com.example.gymapp.helper.DataStoreHelper
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            val (user, pass) = DataStoreHelper.getUserCredentials(this@MainActivity)
            if (!user.isNullOrBlank() && !pass.isNullOrBlank()) {
                val fromCloseAction = intent.getBooleanExtra(CreatePlanActivity.FROM_CLOSE_SESSION, false)
                if (!fromCloseAction) {
                    launchMain()
                }
            }
        }

        val btnLogin: Button = findViewById(R.id.btn_login)
        btnLogin.setOnClickListener {
            etUsername = findViewById(R.id.et_login_username)
            etPassword = findViewById(R.id.et_login_password)
            val username = etUsername.text.toString()
            val userpass = etPassword.text.toString()
            if (validateFields(username, userpass)) {
                lifecycleScope.launch {
                    val (storedUser, storedPass) = DataStoreHelper.getUserCredentials(this@MainActivity)
                    if (username == storedUser && userpass == storedPass) {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.toast_logged_in,
                            Toast.LENGTH_SHORT
                        ).show()
                        launchMain()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            R.string.err_login_incorrect,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else Toast.makeText(
                this@MainActivity,
                R.string.err_login_blank,
                Toast.LENGTH_SHORT
            ).show()
        }

        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            etUsername = findViewById(R.id.et_login_username)
            etPassword = findViewById(R.id.et_login_password)
            val username = etUsername.text.toString()
            val userpass = etPassword.text.toString()
            if (validateFields(username, userpass)) {
                lifecycleScope.launch {
                    DataStoreHelper.saveUserCredentials(this@MainActivity, username, userpass)
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
        val intent = Intent(this, PlanConfirmActivity::class.java)
        startActivity(intent)
    }
}
