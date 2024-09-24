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
        // Initialize the RecyclerView
        val view = inflater.inflate(R.layout.fragment_inicio, container, false)

        // Initialize the RecyclerView
        listMedicamentosHoy = view.findViewById(R.id.ListMedicamentosHoy)
        listMedicamentosHoy.layoutManager = LinearLayoutManager(context)

        val pastillasList: List<Medicamento> = listOf(
            Medicamento(1, "Paracetamol - 200mg", "08:00", "1", 1, "20/09/2024", "08:00", 1),
            Medicamento(2, "Ibuprofeno - 200mg", "12:00", "2", 2, "20/09/2024", "12:00", 0),
            Medicamento(3, "Amoxicilina - 200mg", "18:00", "2", 1, "20/09/2024", "18:00", 1),
            Medicamento(4, "Metformina - 200mg", "20:00", "8", 2, "20/09/2024", "20:00", 0),
            Medicamento(5, "Loratadina - 200mg", "22:00", "1", 1, "20/09/2024", "22:00", 1),
            Medicamento(1, "Paracetamol - 200mg", "08:00", "1", 1, "20/09/2024", "08:00", 1),
            Medicamento(2, "Ibuprofeno - 200mg", "12:00", "2", 2, "20/09/2024", "12:00", 0),
            Medicamento(3, "Amoxicilina - 200mg", "18:00", "2", 1, "20/09/2024", "18:00", 1),
            Medicamento(4, "Metformina - 200mg", "20:00", "8", 2, "20/09/2024", "20:00", 0),
            Medicamento(5, "Loratadina - 200mg", "22:00", "1", 1, "20/09/2024", "22:00", 1)
        )

        adapter = MedicamentoAdapter(pastillasList)
        listMedicamentosHoy.adapter = adapter

        // Obtener el TextView
        val textView4 = view.findViewById<TextView>(R.id.textView4)

        // Obtener la fecha actual
        val currentDate = Calendar.getInstance().time
        val sdf = SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        val formattedDate = sdf.format(currentDate)

        // Establecer el texto del TextView
        textView4.text = formattedDate

        return view
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