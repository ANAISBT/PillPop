package com.example.pillpop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class InicioFragment : Fragment() {
    private lateinit var listMedicamentosHoy: RecyclerView
    private lateinit var adapter: MedicamentoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inicializar el RecyclerView
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        listMedicamentosHoy = view.findViewById(R.id.ListMedicamentosHoy)
        listMedicamentosHoy.layoutManager = LinearLayoutManager(context)

        // Obtener el TextView
        val textView4 = view.findViewById<TextView>(R.id.textView4)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(currentDate)

        // Llamar al servicio para obtener medicamentos
        obtenerMedicamentos(6, formattedDate)

        // Establecer el texto del TextView
        val sdfDisplay = SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        textView4.text = sdfDisplay.format(currentDate)

        return view
    }

    private fun obtenerMedicamentos(pacienteId: Int, fechaHoy: String) {
        // Crear la cola de solicitudes de Volley
        val requestQueue = Volley.newRequestQueue(requireContext())

        // URL del endpoint
        val url = "https://pillpop-backend.onrender.com/obtenerTomasXPacienteFecha" // Reemplaza con la URL real de tu API

        // Crear el objeto JSON que se enviará
        val jsonBody = JSONObject().apply {
            put("pacienteId", pacienteId)
            put("fechaHoy", fechaHoy)
        }

        // Crear la solicitud JSON
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonBody,
            { response ->
                // Procesar la respuesta JSON
                val medicamentosList = mutableListOf<Medicamento>()
                val jsonArray: JSONArray = response.getJSONArray("medicamentos") // Asegúrate de que la respuesta contenga este campo

                for (i in 0 until jsonArray.length()) {
                    val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                    val medicamento = Medicamento(
                        jsonObject.getInt("id"),
                        jsonObject.getString("nombre"),
                        jsonObject.getInt("dosis"),
                        6,
                        fechaHoy,
                        jsonObject.getString("hora_minutos"),
                        jsonObject.getInt("toma")
                    )
                    medicamentosList.add(medicamento)
                }

                // Actualizar el adaptador con los datos obtenidos
                adapter = MedicamentoAdapter(medicamentosList)
                listMedicamentosHoy.adapter = adapter
            },
            { error ->
                // Manejar errores
                Toast.makeText(context, "Error al obtener datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
