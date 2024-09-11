package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TomaPastillaView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_toma_pastilla_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EmpezarTomaBtn)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*val btn: Button = findViewById(R.id.empezarbtn)
        btn.setOnClickListener{
            val intent: Intent = Intent(this, TomaIndicacion_View:: class.java)
            startActivity(intent)
        }*/
    }


}