package com.example.pillpop

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
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
        progressDialog.setMessage("Cargando tomas para el día de hoy...")
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

                        programarNotificacion(medicamento)
                    }

                    // Actualizar el adaptador con los datos obtenidos
                    adapter = MedicamentoAdapter(medicamentosList)
                    adapter.setOnItemClickListener { medicamentoId ->
                        // Manejar el clic en el medicamento
                        val intent = Intent(requireContext(), TomaIndicacionView::class.java).apply {
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
                    //Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                // Manejar errores
                Toast.makeText(context, "Error al obtener las tomas del día de hoy. Cargue nuevamente la vista", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        )

        // Agregar la solicitud a la cola
        requestQueue.add(jsonObjectRequest)
    }

    // Función para programar la notificación
    @SuppressLint("ScheduleExactAlarm")
    private fun programarNotificacion(medicamento: Medicamento) {
        val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
            putExtra("nombreMedicamento", medicamento.medicamento_nombre)
            putExtra("dosisMedicamento", medicamento.dosis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            medicamento.registro_id, // Usa el ID único del medicamento para que no se sobreescriban las notificaciones
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        Log.d("PendingIntent", "Intent: $intent")

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Convierte la hora de toma a milisegundos para programar la alarma
        val horaTomaEnMilisegundos = convertirHoraAMilisegundos(medicamento.hora_toma)
        Log.d("AlarmManager", "Hora de toma: $horaTomaEnMilisegundos")
        if (horaTomaEnMilisegundos > System.currentTimeMillis()) {
            // Solo se programa la alarma si la hora de toma es en el futuro
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                horaTomaEnMilisegundos,
                pendingIntent
            )
        } else {
            Log.d("AlarmManager", "La hora de toma ya pasó, no se programará la notificación.")
        }
    }

    // Convierte la hora en formato de texto a milisegundos
    private fun convertirHoraAMilisegundos(hora: String): Long {
        val formato12Horas = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = formato12Horas.parse(hora)

        // Obtener la cantidad de horas y minutos desde la medianoche
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        val horas = calendar.get(Calendar.HOUR_OF_DAY)
        val minutos = calendar.get(Calendar.MINUTE)

        // Obtener el tiempo actual en milisegundos
        /*val currentTimeMillis = System.currentTimeMillis()

        // Calcular el tiempo en milisegundos desde medianoche hasta la hora de toma
        val horaTomaMillis = (horas * 3_600_000 + minutos * 60_000).toLong()

        // Si la hora de toma ya pasó hoy, no programamos la notificación
        return if (horaTomaMillis < currentTimeMillis) {
            -1L // Retornamos un valor negativo para indicar que no se debe programar la notificación
        } else {
            horaTomaMillis
        }*/
        // Obtener la fecha actual
        val calendarActual = Calendar.getInstance()
        calendarActual.set(Calendar.HOUR_OF_DAY, horas)
        calendarActual.set(Calendar.MINUTE, minutos)
        calendarActual.set(Calendar.SECOND, 0)
        calendarActual.set(Calendar.MILLISECOND, 0)

        // Devolver la hora en milisegundos para programar la alarma
        return calendarActual.timeInMillis
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
