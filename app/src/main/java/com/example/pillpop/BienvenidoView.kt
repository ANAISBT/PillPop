package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class BienvenidoView : AppCompatActivity() {

    private var perfilId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_bienvenido_view)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        // Obtener perfil_id del Intent
        perfilId = intent.getIntExtra("perfil_id", 0)

        val btn: Button = findViewById(R.id.button)
        btn.setOnClickListener{
            val intent = Intent(this, PrincipalView::class.java)
            intent.putExtra("perfil_id", perfilId)
            startActivity(intent)
        }
    }
}