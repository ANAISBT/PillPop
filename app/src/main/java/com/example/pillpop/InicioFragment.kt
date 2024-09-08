package com.example.pillpop

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InicioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InicioFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var listMedicamentosHoy: RecyclerView
    private lateinit var adapter: MedicamentoAdapter
    private var param1: String? = null
    private var param2: String? = null
    private var perfilId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            perfilId = it.getInt("perfil_id")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the RecyclerView
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Initialize the RecyclerView
        listMedicamentosHoy = view.findViewById(R.id.ListMedicamentosHoy)
        listMedicamentosHoy.layoutManager = LinearLayoutManager(context)

        // Obtener el TextView
        val textView4 = view.findViewById<TextView>(R.id.textView4)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        val formattedDate = sdf.format(currentDate)

        // Establecer el texto del TextView
        textView4.text = formattedDate

        fetchMedicamentos()

        return view
    }

    private fun fetchMedicamentos() {
        // Create a new thread or use a coroutine for network operations
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://pillpop.000webhostapp.com/pillpop/prescripcionesPorFecha.php?paciente_id=$perfilId")
                .build()
            val response: Response = client.newCall(request).execute()
            val jsonData = response.body?.string()

            jsonData?.let {
                val gson = Gson()
                val listType: Type = object : TypeToken<List<Medicamento>>() {}.type
                val medicamentos: List<Medicamento> = gson.fromJson(it, listType)

                activity?.runOnUiThread {
                    adapter = MedicamentoAdapter(medicamentos)
                    listMedicamentosHoy.adapter = adapter

                    // Set the click listener for items in the adapter
                    adapter.setOnItemClickListener { position ->
                        val selectedMedicamento = medicamentos[position]
                        val registroId = selectedMedicamento.registro_id

                        // Inicia la nueva actividad
                        val intent = Intent(requireContext(), TomaIndicacionView::class.java)
                        intent.putExtra("registro_id", registroId)
                        intent.putExtra("perfil_id", perfilId)
                        startActivity(intent)
                    }
                }
            }
        }.start()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}