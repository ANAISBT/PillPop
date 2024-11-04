package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class BienvenidoView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenido_view)

        // Encuentra el botón por su ID
        val button = findViewById<Button>(R.id.buttonIngresar)

        // Agrega un listener para el botón
        button.setOnClickListener {
            // Intenta navegar a otra actividad
            val intent = Intent(this@BienvenidoView, PrincipalView::class.java)
            startActivity(intent)  // Inicia la nueva actividad
        }

    }



}