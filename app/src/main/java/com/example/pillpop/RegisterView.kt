package com.example.pillpop

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import android.util.Patterns

class RegisterView : AppCompatActivity() {

    private val generoMap = HashMap<String, Int>()  // Mapa para almacenar nombres de géneros y sus IDs
    private lateinit var nombreInput: EditText
    private lateinit var spinnerGenero: Spinner
    private lateinit var edadInput: EditText
    private lateinit var dniInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var btnRegistrar: Button

    private lateinit var nombreErrorText: TextView
    private lateinit var edadErrorText: TextView
    private lateinit var dniErrorText: TextView
    private lateinit var emailErrorText: TextView
    private lateinit var passwordErrorText: TextView

    //private var generoId: Int = 1
    //private var tipoPerfilId: Int = 1
    private lateinit var requestQueue: RequestQueue
    //private var correct = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_view)

        spinnerGenero = findViewById(R.id.generosDrop)
        nombreInput = findViewById(R.id.NombreInput)
        edadInput = findViewById(R.id.EdadInput)
        dniInput = findViewById(R.id.DNIInput)
        emailInput = findViewById(R.id.EmailAddressInput)
        passwordInput = findViewById(R.id.editTextTextPassword)

        nombreErrorText = findViewById(R.id.nombreErrorText)
        edadErrorText = findViewById(R.id.edadErrorText)
        dniErrorText = findViewById(R.id.dniErrorText)
        emailErrorText = findViewById(R.id.emailErrorText)
        passwordErrorText = findViewById(R.id.passwordErrorText)

        // Asignar los TextWatchers usando una función común
        setupTextWatcher(nombreInput, nombreErrorText)
        setupTextWatcher(edadInput, edadErrorText)
        setupTextWatcher(dniInput, dniErrorText)
        setupTextWatcher(emailInput, emailErrorText)
        setupTextWatcher(passwordInput, passwordErrorText)

        // Inicializamos la requestQueue de Volley
        requestQueue = Volley.newRequestQueue(this)

        // Cargar los datos desde las rutas usando Volley
        loadGeneros()

        btnRegistrar = findViewById(R.id.Registrarse_btn)

        btnRegistrar.setOnClickListener {

            // Obtén el valor seleccionado en el Spinner de Género
            val generoSeleccionado = spinnerGenero.selectedItem.toString()
            val idGenero = generoMap[generoSeleccionado]

            val nombre = nombreInput.text.toString().trim()
            val edad = edadInput.text.toString().trim()
            val dni = dniInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            var hayError = validarCampos(nombre, edad, dni, email, password)

            if (!hayError){
                registrarPaciente(nombre, idGenero, edad, dni, email, password)
            }

        }

    }

    // Función para configurar TextWatcher y limpiar el mensaje de error
    private fun setupTextWatcher(inputField: EditText, errorText: TextView) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                errorText.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Función para obtener los géneros usando Volley
    private fun loadGeneros() {
        val url = "https://pillpop-backend.onrender.com/getDataSexo"  // Reemplaza con la URL correcta

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val generosList = ArrayList<String>()
                    for (i in 0 until response.length()) {
                        val generoObject: JSONObject = response.getJSONObject(i)
                        val id = generoObject.getInt("id")  // Obtener el ID del género
                        val nombre = generoObject.getString("nombre")
                        generosList.add(nombre)
                        generoMap[nombre] = id
                    }
                    // Configuramos el adaptador del Spinner
                    val adapterGeneros = ArrayAdapter(this, android.R.layout.simple_spinner_item, generosList)
                    spinnerGenero.adapter = adapterGeneros
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Manejo de errores
                error.printStackTrace()
            }
        )

        // Añadimos la petición a la cola de Volley
        requestQueue.add(jsonArrayRequest)
    }

    private fun registrarPaciente(nombre: String, idGenero: Int?, edad: String, dni: String, email: String, password: String) {
        val url = "https://pillpop-backend.onrender.com/insertarPaciente"  // Reemplaza con la URL correcta

        // Crea un objeto JSON con los datos del paciente
        val jsonObject = JSONObject()
        try {
            jsonObject.put("nombreCompleto", nombre)
            jsonObject.put("sexo_id", idGenero)
            jsonObject.put("edad", edad)
            jsonObject.put("dni", dni)
            jsonObject.put("correoElectronico", email)
            jsonObject.put("contrasena", password)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Crear una petición POST con Volley
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonObject,
            { response ->
                // Manejar la respuesta exitosa
                try {
                    val mensaje = response.getString("mensaje")
                    Idpaciente = response.getInt("idUsuarioPaciente")
                    Nombrepaciente = nombre
                    if(idGenero == 1){
                        Sexopaciente = "Masculino"
                    } else if(idGenero == 2){
                        Sexopaciente = "Femenino"
                    }
                    Edadpaciente = edad.toIntOrNull()
                    Dnipaciente = dni
                    Correopaciente = email

                    Log.d("Registro", "$mensaje con ID: $Idpaciente")
                    // Redirigir al usuario a la vista de bienvenida
                    val intent = Intent(this, BienvenidoView::class.java)
                    startActivity(intent)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            { error ->
                // Manejar errores
                Log.e("Registro", "Error al registrar el paciente: ${error.message}")
            }
        )

        // Añadir la petición a la cola de Volley
        requestQueue.add(jsonObjectRequest)
    }

    private fun validarCampos(nombre: String, edad: String, dni: String, email: String, password: String): Boolean {

        nombreErrorText.visibility = View.GONE
        edadErrorText.visibility=View.GONE
        dniErrorText.visibility=View.GONE
        emailErrorText.visibility=View.GONE
        passwordErrorText.visibility=View.GONE

        val edadInt: Int?= edad.toIntOrNull()
        var hayError = false // Variable para rastrear si hay errores

        // Validaciones
        if (nombre.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            nombreErrorText.text = mensaje
            nombreErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }

        if (edad.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            edadErrorText.text = mensaje
            edadErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }else if (edadInt != null && edadInt < 18) {
            val mensaje = "Debe ser mayor de edad."
            edadErrorText.text = mensaje
            edadErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }

        if (dni.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            dniErrorText.text = mensaje
            dniErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        } else if (dni.length != 8) {
            val mensaje = "El DNI debe tener exactamente 8 dígitos."
            dniErrorText.text = mensaje
            dniErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }

        if (email.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            emailErrorText.text = mensaje
            emailErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            val mensaje = "Formato de correo electrónico no válido"
            emailErrorText.text = mensaje
            emailErrorText.visibility = View.VISIBLE
            hayError = true
        }

        if (password.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            passwordErrorText.text = mensaje
            passwordErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }

        return hayError
    }

}

