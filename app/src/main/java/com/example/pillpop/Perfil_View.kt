package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil_View : AppCompatActivity() {
    private lateinit var btPaciente: Button
    private lateinit var btDoctor: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_perfil_view)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        btPaciente = findViewById(R.id.id_Paciente)
        btDoctor = findViewById(R.id.id_Doctor)

        // Configura el OnClickListener para el botón Paciente.
        btPaciente.setOnClickListener {
            val intent = Intent(this, DatosView::class.java)
            startActivity(intent)
        }

    }
}