package com.example.pillpop

import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.OutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
class LoginTask(private val email: String, private val password: String, private val onLoginComplete: (Int?) -> Unit) : AsyncTask<Void, Void, String>() {

    override fun doInBackground(vararg params: Void?): String {
        val urlString = "https://pillpop.000webhostapp.com/pillpop/login.php"
        var result = ""

        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            connection.doOutput = true

            // Enviar datos
            val postData = "correo=$email&contraseña=$password"
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(postData.toByteArray())
            outputStream.flush()
            outputStream.close()

            // Leer la respuesta
            val inputStream = connection.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }
            reader.close()

            result = sb.toString()
        } catch (e: Exception) {
            Log.e("LoginTask", "Error", e)
        }

        return result
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        try {
            val jsonResponse = JSONObject(result)
            if (jsonResponse.has("perfil_id")) {
                val perfilId = jsonResponse.getInt("perfil_id")
                onLoginComplete(perfilId)
            } else {
                // Manejar el error, por ejemplo, mostrar un mensaje
                val error = jsonResponse.getString("error")
                Log.e("LoginTask", "Login error: $error")
                onLoginComplete(null)
            }
        } catch (e: Exception) {
            Log.e("LoginTask", "Error parsing JSON", e)
            onLoginComplete(null)
        }
    }
}