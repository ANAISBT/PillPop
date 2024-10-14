package com.example.pillpop

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class CambiarContraseñaView : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog
    private lateinit var requestQueue: RequestQueue
    private lateinit var dniChangeInput: EditText
    private lateinit var contrasenaEditInput: EditText
    private lateinit var CancelarButton: Button
    private lateinit var EditarContrasenaButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contrasena_view)
    // Inicializamos la requestQueue de Volley
        requestQueue = Volley.newRequestQueue(this)

        // Inicializar el ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando datos...")
        progressDialog.setCancelable(false)

        // Obtener referencias a los elementos de la interfaz
        dniChangeInput = findViewById(R.id.DNIChangeInput)
        contrasenaEditInput = findViewById(R.id.ContrasenaEditInput)

        // Llamar a obtenerDatosDoctor para cargar los datos del doctor
        obtenerDatosPaciente()

        CancelarButton = findViewById(R.id.CancelarContrasenaEditBtn)
        CancelarButton.setOnClickListener {
            // Cerrar la actividad y regresar a la anterior
            finish()
        }

        EditarContrasenaButton= findViewById(R.id.EditarContrasenaButton)
        EditarContrasenaButton.setOnClickListener {
            val nuevaContrasena = contrasenaEditInput.text.toString()
            if (nuevaContrasena.length <= 6) {
                // Mostrar un mensaje de error, por ejemplo, usando un Toast
                Toast.makeText(this, "La nueva contraseña debe tener más de 6 caracteres", Toast.LENGTH_SHORT).show()
            } else {
                // Cerrar la actividad y regresar a la anterior
                Idpaciente?.let { it1 -> editarContrasenaPaciente(it1, nuevaContrasena) }
            }
        }
    }
    private fun editarContrasenaPaciente(id: Int, nuevaContrasena: String) {
        progressDialog.setMessage("Actualizando contraseña...")
        progressDialog.show()

        // La URL de tu nuevo endpoint
        val url = "https://pillpop-backend.onrender.com/editarContrasenaPaciente/$id"

        // Crear el objeto JSON para el cuerpo de la solicitud
        val jsonBody = JSONObject()
        jsonBody.put("contrasena", nuevaContrasena) // Asegúrate de usar "contrasena" como en el backend

        // Crear la solicitud StringRequest
        val stringRequest = object : StringRequest(
            Method.PUT,
            url,
            { response ->
                // Manejar la respuesta del servidor
                Toast.makeText(this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
                finish() // Finaliza la actividad o cierra el diálogo
            },
            { error ->
                // Manejar el error
                Log.e("CambiarContrasenaView", "Error: ${error.message}")
                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        ) {
            // Convertir el JSON a un arreglo de bytes
            override fun getBody(): ByteArray {
                return jsonBody.toString().toByteArray(Charsets.UTF_8)
            }

            // Establecer el tipo de contenido de la solicitud
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        // Agregar la solicitud a la cola de Volley
        requestQueue.add(stringRequest)
    }

    private fun obtenerDatosPaciente() {
        progressDialog.show()
        val url = "https://pillpop-backend.onrender.com/paciente/$Idpaciente" // Cambia a la ruta del backend

        // Crear la solicitud GET
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET,
            url,
            null, // No necesitas un cuerpo para GET
            { response ->
                // Manejar la respuesta JSON que es un Array
                if (response.length() > 0) {
                    val pacienteJson = response.getJSONObject(0) // Obtener el primer objeto del array
                    val paciente = Paciente(
                        id = pacienteJson.getInt("id"),
                        nombreCompleto = pacienteJson.getString("nombreCompleto"),
                        sexo_id = pacienteJson.getInt("sexo_id"),
                        edad = pacienteJson.getInt("edad"), // Agregar el campo "edad"
                        dni = pacienteJson.getInt("dni"),
                        correoElectronico = pacienteJson.getString("correoElectronico")
                    )
                    // Llenar los campos de entrada con los datos del paciente
                    dniChangeInput.setText(paciente.dni.toString()) // Asignar el DNI
                    // Aquí puedes llenar otros campos si es necesario
                    contrasenaEditInput.setText("") // Puedes dejar el campo de contraseña vacío por ahora

                    progressDialog.dismiss()
                } else {
                    Toast.makeText(this, "Paciente no encontrado", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            },
            { error ->
                // Manejar el error
                Log.e("CambiarContrasenaView", "Error: ${error.message}")
                Toast.makeText(this, "Error al obtener los datos del paciente", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        )

        // Agregar la solicitud a la cola de Volley
        requestQueue.add(jsonRequest)
    }


}