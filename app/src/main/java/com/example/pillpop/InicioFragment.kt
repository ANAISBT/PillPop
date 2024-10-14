package com.example.pillpop

import android.app.ProgressDialog
import android.content.Intent
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
    private lateinit var progressDialog: ProgressDialog

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

        // Inicializar el ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Cargando datos...")
        progressDialog.setCancelable(false)

        // Obtener el TextView
        val textView4 = view.findViewById<TextView>(R.id.textView4)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = sdf.format(currentDate)

        // Llamar al servicio para obtener medicamentos
        val id = Idpaciente
        if (id != null) {
            obtenerMedicamentos(id, formattedDate)
        }

        // Establecer el texto del TextView
        val sdfDisplay = SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        textView4.text = sdfDisplay.format(currentDate)

        return view
    }

    private fun obtenerMedicamentos(pacienteId: Int, fechaHoy: String) {
        progressDialog.show()
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
                // Verificar si la respuesta contiene la lista de medicamentos
                if (response.has("medicamentos")) {
                    val medicamentosList = mutableListOf<Medicamento>()
                    val jsonArray: JSONArray = response.getJSONArray("medicamentos")

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                        var horaToma = jsonObject.getString("hora_minutos")
                        horaToma = convertirHoraFormato12Horas(horaToma) // Convierte la hora a formato 12 horas
                        val medicamento = Medicamento(
                            jsonObject.getInt("id"),
                            jsonObject.getString("nombre"),
                            jsonObject.getInt("dosis"),
                            pacienteId,
                            fechaHoy,
                            horaToma,
                            jsonObject.getInt("toma")
                        )
                        medicamentosList.add(medicamento)
                    }

                    // Actualizar el adaptador con los datos obtenidos
                    adapter = MedicamentoAdapter(medicamentosList)
                    adapter.setOnItemClickListener { medicamentoId ->
                        // Manejar el clic en el medicamento
                        val intent = Intent(context, TomaIndicacionView::class.java).apply {
                            putExtra("ID_MEDICAMENTO", medicamentoId) // Pasa el ID del medicamento
                        }
                        startActivity(intent) // Inicia la actividad de indicaciones
                    }
                    listMedicamentosHoy.adapter = adapter
                    progressDialog.dismiss()
                } else if (response.has("mensaje")) {
                    // Si no se encontraron medicamentos, muestra el mensaje
                    val mensaje = response.getString("mensaje")
                    progressDialog.dismiss()
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Manejar errores
                Toast.makeText(context, "Error al obtener datos: ${error.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        )

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest)
    }

    private fun convertirHoraFormato12Horas(hora24: String): String {
        // Define los formatos para 24 horas y 12 horas
        val formato24Horas = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formato12Horas = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Convierte la cadena de 24 horas al formato de fecha/hora
        val date = formato24Horas.parse(hora24)

        // Retorna la hora en formato de 12 horas
        return formato12Horas.format(date!!)
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
