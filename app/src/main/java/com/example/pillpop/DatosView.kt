package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class DatosView : AppCompatActivity() {
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
        setContentView(R.layout.activity_datos_view)

        nombreInput = findViewById<EditText>(R.id.NombreInput)
        correoInput = findViewById<EditText>(R.id.EmailAddressInput)
        contrasenaInput = findViewById<EditText>(R.id.editTextTextPassword)



        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.EmpezarTomaBtn)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the Spinner
        val genderSpinner: Spinner = findViewById(R.id.generosDrop)


        // Create an ArrayAdapter using the string array and the custom spinner layout
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_array,
            R.layout.spiner_selected_item // Use your custom spinner item layout for the selected item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Use the same layout for dropdown items
        }

        // Apply the adapter to the spinner
        genderSpinner.adapter = adapter

        val generoSeleccionado = genderSpinner.selectedItem.toString()

        // Comparar cadenas usando comillas dobles y el operador ==
        generoId = when (generoSeleccionado) {
            "Femenino" -> 1
            "Masculino" -> 2
            "Otro" -> 3
            else -> 0 // Valor predeterminado en caso de que no coincida con ninguna opción
        }


        val btn: Button = findViewById(R.id.Registrarse_btn)
        btn.setOnClickListener{
            // Inicializar la RequestQueue
            requestQueue = Volley.newRequestQueue(this)
            ejecutarServicio("https://pillpop.000webhostapp.com/pillpop/insertarDatosPerfil.php")
            /*if(correct){
                val intent: Intent = Intent(this, Bienvenido_View:: class.java)
                startActivity(intent)
            }*/
        }
    }


    private fun ejecutarServicio(url: String) {
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            { response ->
                try {
                    // Parsear la respuesta JSON
                    val jsonResponse = JSONObject(response)

                    if (jsonResponse.getBoolean("success")) {
                        val perfilId = jsonResponse.getInt("perfil_id")

                        // Mostrar mensaje de éxito
                        Toast.makeText(applicationContext, "Registro exitoso. Perfil ID: $perfilId", Toast.LENGTH_SHORT).show()

                        // Pasar perfil_id a Bienvenido_View
                        val intent = Intent(this, BienvenidoView::class.java)
                        intent.putExtra("perfil_id", perfilId)
                        startActivity(intent)
                    } else {
                        // Mostrar mensaje de error
                        val errorMessage = jsonResponse.getString("error")
                        Toast.makeText(applicationContext, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Manejar errores de parseo JSON
                    Toast.makeText(applicationContext, "Error en el formato de la respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(applicationContext, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                return mapOf(
                    "nombres" to nombreInput.text.toString(),
                    "correo" to correoInput.text.toString(),
                    "contraseña" to contrasenaInput.text.toString(),
                    "genero_id" to generoId.toString(),
                    "tipo_perfil_id" to tipoPerfilId.toString()
                )
            }
        }

        // Add the request to the RequestQueue
        requestQueue.add(stringRequest)
    }




}