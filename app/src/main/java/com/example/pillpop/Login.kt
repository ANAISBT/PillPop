package com.example.pillpop

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    private lateinit var edtDni: EditText
    private lateinit var edtContrasena: EditText
    private lateinit var btnLogin: Button

    private lateinit var dniErrorText: TextView
    private lateinit var passwordErrorText: TextView
    private lateinit var requestQueue: RequestQueue
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        edtDni = findViewById(R.id.DNIInput)
        edtContrasena = findViewById(R.id.TextPasswordInput)
        btnLogin = findViewById(R.id.IniciarSesionBtn)

        dniErrorText = findViewById(R.id.dniErrorText)
        passwordErrorText = findViewById(R.id.passwordErrorText)

        setupTextWatcher(edtDni, dniErrorText)
        setupTextWatcher(edtContrasena, passwordErrorText)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando datos...")
        progressDialog.setCancelable(false) // Evitar que el usuario lo pueda cancelar

        btnLogin.setOnClickListener {
            val dni = edtDni.text.toString()
            val contrasena = edtContrasena.text.toString()

            var hayError = validarCampos(dni, contrasena)

            if (!hayError){
                iniciarSesion(dni, contrasena)
            }
        }

    }
    private fun iniciarSesion(dni: String, contrasena: String) {
        progressDialog.show()
        val queue = Volley.newRequestQueue(this)
        val url = "https://pillpop-backend.onrender.com/loginPaciente"

        // Crear los datos JSON para enviar en el body
        val jsonBody = JSONObject()
        jsonBody.put("p_dni", dni)
        jsonBody.put("p_contrasena", contrasena)

        // Crear la solicitud POST
        val request = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                try {
                    val mensaje = response.getString("mensaje")
                    if (mensaje == "Login exitoso") {
                        val pacienteId = response.getInt("id")
                        Idpaciente=pacienteId
                        Nombrepaciente = response.getString("nombreCompleto")
                        var idGenero = response.getString("sexo_id").toIntOrNull()
                        if(idGenero == 1){
                            Sexopaciente = "Masculino"
                        } else if(idGenero == 2){
                            Sexopaciente = "Femenino"
                        }
                        Edadpaciente = response.getString("edad").toIntOrNull()
                        Dnipaciente = response.getString("dni")
                        Correopaciente = response.getString("correoElectronico")

                        // Navegar a la vista de bienvenida o la siguiente pantalla
                        val intent = Intent(this, PrincipalView::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error en la respuesta: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                finally {
                    progressDialog.dismiss()
                }
            },
            { error ->
                if (error.networkResponse != null) {
                    val statusCode = error.networkResponse.statusCode
                    val errorMsg = String(error.networkResponse.data)
                    Log.e("VolleyError", "Error code: $statusCode, Error message: $errorMsg")
                    Toast.makeText(this, "Error en el servidor: $statusCode", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                } else {
                    Log.e("VolleyError", "Error: ${error.message}")
                    Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_LONG).show()
                    progressDialog.dismiss()
                }
            }
        )

        // Añadir la solicitud a la cola de Volley
        queue.add(request)
    }

    private fun setupTextWatcher(inputField: EditText, errorText: TextView) {
        inputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                errorText.visibility = View.GONE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun validarCampos(dni: String, password: String): Boolean {

        dniErrorText.visibility=View.GONE
        passwordErrorText.visibility=View.GONE

        var hayError = false // Variable para rastrear si hay errores

        // Validaciones

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

        if (password.isEmpty()) {
            val mensaje = "El campo no debe estar vacío"
            passwordErrorText.text = mensaje
            passwordErrorText.visibility = View.VISIBLE
            hayError = true // Marca que hay un error
        }

        return hayError
    }
}