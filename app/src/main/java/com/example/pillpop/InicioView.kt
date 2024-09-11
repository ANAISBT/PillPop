package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InicioView: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_view)

        // Configurar el clic para el TextView "Iniciar Sesión"
        val iniciarSesion: TextView = findViewById(R.id.IniciarSesion)
        iniciarSesion.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        // Configurar el clic para el Button "Iniciar"
        val btn: Button = findViewById(R.id.buttonInicio)
        btn.setOnClickListener {
            val intent = Intent(this, Perfil_View::class.java)
            startActivity(intent)
        }
    }
}