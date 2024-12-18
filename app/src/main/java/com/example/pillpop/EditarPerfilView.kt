package com.example.pillpop

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

class EditarPerfilView : AppCompatActivity() {
    private lateinit var CancelarButton: Button
    private lateinit var EditarPerfilButton: Button
    private lateinit var nombreCompletoEditInput: EditText
    private lateinit var dniEditInput: EditText
    private lateinit var edadEditInput: EditText
    private lateinit var correoElectronicoEditInput: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        // Vincular los elementos de la interfaz de usuario
        nombreCompletoEditInput = findViewById(R.id.nombreCompletoEditInput)
        dniEditInput = findViewById(R.id.DNIEditInput)
        edadEditInput = findViewById(R.id.EdadEditInput)
        correoElectronicoEditInput = findViewById(R.id.EmailAddressEditInput)
        spinnerGenero= findViewById(R.id.generosEditDrop)


        // Configurar el Spinner con los géneros
        val generosList = resources.getStringArray(R.array.gender_array)
        val adapterGeneros = ArrayAdapter(this,R.layout.spinner_item, generosList)
        adapterGeneros.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerGenero.adapter = adapterGeneros

        // Inicializar el ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando datos...")
        progressDialog.setCancelable(false)

        Idpaciente?.let {
            obtenerDatosPaciente(this, it,
                onSuccess = { paciente ->
                    paciente?.let {
                        // Llenar los campos con los datos obtenidos
                        nombreCompletoEditInput.setText(it.nombreCompleto)
                        dniEditInput.setText(it.dni.toString())
                        edadEditInput.setText(it.edad.toString())
                        correoElectronicoEditInput.setText(it.correoElectronico)

                        // Seleccionar el género en el Spinner
                        val generoSeleccionado = if (it.sexo_id == 1) "Masculino" else "Femenino"
                        val spinnerPosition = generosList.indexOf(generoSeleccionado)
                        spinnerGenero.setSelection(spinnerPosition)
                        progressDialog.dismiss()
                    }?: run {
                        Toast.makeText(this, "No se encontraron datos del paciente", Toast.LENGTH_LONG).show()
                        finish()
                    }
                },
                onError = { errorMessage ->
                    Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_LONG).show()
                    finish()
                }
            )
        }

        CancelarButton = findViewById(R.id.CancelarEditBtn)
        CancelarButton.setOnClickListener {
            // Cerrar la actividad y regresar a la anterior
            finish()
        }

        EditarPerfilButton= findViewById(R.id.EditarPerfilButton)
        EditarPerfilButton.setOnClickListener {
            // Validaciones de entrada
            val nombreCompleto = nombreCompletoEditInput.text.toString().trim()
            val dni = dniEditInput.text.toString().toIntOrNull()
            val edad = edadEditInput.text.toString().toIntOrNull()
            val correoElectronico = correoElectronicoEditInput.text.toString().trim()
            val sexoId = spinnerGenero.selectedItemPosition + 1

            // Validaciones
            when {
                nombreCompleto.isEmpty() -> {
                    Toast.makeText(this, "El nombre completo no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                dni == null || dni.toString().length != 8 -> {
                    Toast.makeText(this, "El DNI debe ser un número de 8 dígitos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                edad == null || edad <= 0 -> {
                    Toast.makeText(this, "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(correoElectronico).matches() -> {
                    Toast.makeText(this, "Correo electrónico no válido", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else -> {
                    val paciente = Idpaciente?.let { Paciente(it, nombreCompleto, sexoId, edad, dni, correoElectronico) }

                    Idpaciente?.let { id ->
                        paciente?.let {
                            editarDatosPaciente(this, id, it,
                                onSuccess = { mensaje ->
                                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
                                    finish()
                                },
                                onError = { errorMessage ->
                                    Toast.makeText(this, "Error al actualizar los datos: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    fun obtenerDatosPaciente(context: Context, id: Int, onSuccess: (Paciente?) -> Unit, onError: (String) -> Unit) {
        progressDialog.setMessage("Onteniendo datos del paciente...")
        progressDialog.show()
        val url = "https://pillpop-backend.onrender.com/paciente/$id"

        // Crear la cola de solicitudes
        val queue: RequestQueue = Volley.newRequestQueue(context)

        // Solicitud JSON a la API para un JSONArray
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response: JSONArray ->
                try {
                    // Verificar si el array contiene al menos un elemento
                    if (response.length() > 0) {
                        val pacienteJson = response.getJSONObject(0) // Obtener el primer objeto JSON del array
                        val paciente = Gson().fromJson(pacienteJson.toString(), Paciente::class.java)
                        onSuccess(paciente)
                    } else {
                        onError("Paciente no encontrado.")
                    }
                } catch (e: Exception) {
                    onError("Error al parsear los datos del paciente: ${e.message}")
                }
            },
            { error ->
                onError("Error en la solicitud: ${error.message}")
            }
        )

        // Añadir la solicitud a la cola
        queue.add(jsonArrayRequest)
    }

    fun editarDatosPaciente(context: Context, id: Int, paciente: Paciente, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        progressDialog.setMessage("Actualizando Perfil...")
        progressDialog.show()
        val url = "https://pillpop-backend.onrender.com/editarpaciente/$id"

        // Crear el objeto JSON con los datos del paciente
        val jsonObject = JSONObject().apply {
            put("nombreCompleto", paciente.nombreCompleto)
            put("sexo_id", paciente.sexo_id)
            put("edad", paciente.edad)
            put("dni", paciente.dni)
            put("correoElectronico", paciente.correoElectronico)
        }

        // Crear la cola de solicitudes
        val queue: RequestQueue = Volley.newRequestQueue(context)

        // Solicitud JSON a la API
        val jsonObjectRequest = object : JsonObjectRequest(
            Method.PUT, url, jsonObject,
            { response ->
                try {
                    val mensaje = response.getString("mensaje")
                    onSuccess(mensaje)
                } catch (e: Exception) {
                    onError("Error al procesar la respuesta: ${e.message}")
                }
            },
            { error ->
                onError("Error en la solicitud: ${error.message}")
            }
        ) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        // Añadir la solicitud a la cola
        queue.add(jsonObjectRequest)
    }

}