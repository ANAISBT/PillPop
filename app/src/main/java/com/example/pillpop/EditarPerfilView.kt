package com.example.pillpop

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class EditarPerfilView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        val spinnerGenero : Spinner = findViewById(R.id.generosEditDrop)
        val generosList= resources.getStringArray(R.array.gender_array)
        val adapterGeneros = ArrayAdapter(this,android.R.layout.simple_spinner_item,generosList)
        spinnerGenero.adapter = adapterGeneros



    }
}