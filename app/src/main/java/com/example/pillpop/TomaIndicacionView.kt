package com.example.pillpop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class TomaIndicacionView : AppCompatActivity() {
    private lateinit var medicamentoId: String
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toma_indicacion_view)

        val button = findViewById<Button>(R.id.IrVerificacionBtn)

        // Recibir el ID del medicamento
        medicamentoId = intent.getStringExtra("ID_MEDICAMENTO") ?: "No ID"

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("ID_MEDICAMENTO", medicamentoId) // Pasa el ID del medicamento
            }
            startActivity(intent) // Inicia MainActivity

        }
    }
}