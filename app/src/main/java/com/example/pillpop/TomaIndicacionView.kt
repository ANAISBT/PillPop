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
    private var perfilId: Int = 0
    private var registro_id: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toma_indicacion_view)

        // Obtener registro_id del Intent
        registro_id = intent.getIntExtra("registro_id", 0)
        perfilId = intent.getIntExtra("perfil_id", 0)

        val btn: Button = findViewById(R.id.IrVerificacionBtn)
        btn.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("registro_id", registro_id)
            intent.putExtra("perfil_id", perfilId)
            startActivity(intent)
        }


    }
}