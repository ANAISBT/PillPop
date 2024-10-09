package com.example.pillpop

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.google.gson.Gson

class BienvenidoView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenido_view)

        // Referencia al botón
        val empezarButton: Button = findViewById(R.id.button)

        // Establecer el click listener
        empezarButton.setOnClickListener {

            // Obtener el pacienteId desde SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val pacienteId = sharedPreferences.getInt("PACIENTE_ID", -1)

            if (pacienteId != -1) {
                // Crear el Intent para navegar a PrincipalView
                val intent = Intent(this, PrincipalView::class.java)
                usarPacienteId(pacienteId)
            } else {
                Toast.makeText(this, "No se encontró el ID del paciente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun usarPacienteId(pacienteId: Int) {
        val queue = Volley.newRequestQueue(this)

        val urlTomas =  "https://pillpop-backend.onrender.com/obtenerTomasXPacienteFecha"

        val fechaActual = LocalDate.now()
        val formato = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val fechaHoy = fechaActual.format(formato)

        // Crear los datos JSON para enviar en el body
        val jsonBody = JSONObject()
        jsonBody.put("pacienteId", pacienteId)
        jsonBody.put("fechaHoy", fechaHoy)


        // Crear la solicitud POST
        val request = JsonObjectRequest(
            Request.Method.POST, urlTomas, jsonBody,
            { response ->
                try {

                    val tomasArray = response.getJSONArray("tomas")

                    val tomasList = mutableListOf<Toma>() // Lista para almacenar las tomas
                    for (i in 0 until tomasArray.length()) {
                        val tomaObject = tomasArray.getJSONObject(i)

                        // Crear un objeto Toma a partir del JSON
                        val toma = Toma(
                            id = tomaObject.getInt("id"),
                            nombre = tomaObject.getString("nombre"),
                            horaMinutos = tomaObject.getString("hora_minutos"),
                            dosis = tomaObject.getInt("dosis"),
                            toma = tomaObject.getInt("toma")
                        )
                        tomasList.add(toma) // Agregar a la lista
                    }

                    // Convertir la lista a JSON usando Gson
                    val gson = Gson()
                    val tomasListJson = gson.toJson(tomasList)

                    // Enviar los datos a la siguiente actividad
                    val intent = Intent(this, PrincipalView::class.java)
                    intent.putExtra("tomasListJson", tomasListJson)
                    startActivity(intent)




                } catch (e: Exception) {
                    Toast.makeText(this, "Error en la respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                if (error.networkResponse != null) {
                    val statusCode = error.networkResponse.statusCode
                    val errorMsg = String(error.networkResponse.data)
                    Log.e("VolleyError", "Error code: $statusCode, Error message: $errorMsg")
                    Toast.makeText(this, "Error en el servidor: $statusCode", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("VolleyError", "Error: ${error.message}")
                    Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
        // Añadir la solicitud a la cola de Volley
        queue.add(request)

    }
}

// Definir la clase Toma para almacenar los datos
data class Toma(
    val id: Int,
    val nombre: String,
    val horaMinutos: String,
    val dosis: Int,
    val toma: Int
)