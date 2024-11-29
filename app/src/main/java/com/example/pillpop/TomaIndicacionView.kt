package com.example.pillpop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class TomaIndicacionView : AppCompatActivity() {
    private var idMedicamento: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toma_indicacion_view)

        val button = findViewById<Button>(R.id.IrVerificacionBtn)

        // Obtener el ID del medicamento de los extras
        idMedicamento = intent.getIntExtra("ID_MEDICAMENTO", -1) 

        if (idMedicamento != -1) {
        }

        button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("ID_MEDICAMENTO", idMedicamento) // Pasa el ID del medicamento
            }
            startActivity(intent) // Inicia MainActivity

        }
    }
}