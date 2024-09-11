package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailInput: EditText = findViewById(R.id.correoElectrónicoInput)
        val passwordInput: EditText = findViewById(R.id.TextPasswordInput)
        val loginButton: Button = findViewById(R.id.IniciarSesionBtn)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            // Ejecutar LoginTask
            LoginTask(email, password) { perfilId ->
                if (perfilId != null) {
                    // Login exitoso, redirigir al PrincipalView
                    val intent = Intent(this, PrincipalView::class.java)
                    intent.putExtra("perfil_id", perfilId)
                    startActivity(intent)
                } else {
                    // Mostrar mensaje de error al usuario
                }
            }.execute()
        }
    }
}