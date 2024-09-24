package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class RegisterView : AppCompatActivity() {
    private lateinit var nombreInput: EditText
    private lateinit var correoInput: EditText
    private lateinit var contrasenaInput: EditText
    private var generoId: Int = 1
    private var tipoPerfilId: Int = 1
    private lateinit var requestQueue: RequestQueue
    private var correct = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)

        val spinnerGenero : Spinner = findViewById(R.id.generosDrop)

        val generosList= resources.getStringArray(R.array.gender_array)

        val adapterGeneros = ArrayAdapter(this,android.R.layout.simple_spinner_item,generosList)

        spinnerGenero.adapter = adapterGeneros

    }

}