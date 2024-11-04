package com.example.pillpop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView
import java.util.Calendar

class MedicamentoAdapter(private val medicamentos: List<Medicamento>) : RecyclerView.Adapter<MedicamentoAdapter.ViewHolder>() {

    // Define un listener para los clics en los ítems
    private var onItemClickListener: ((Int) -> Unit)? = null

    // Método para configurar el listener
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_medicamento,viewGroup,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val medicamento = medicamentos[position]
        viewHolder.nombrePill.text = medicamento.medicamento_nombre
        viewHolder.dosisPill.text = "${medicamento.dosis} unidades"

        // Extraer hora y minutos de medicamento.hora_toma y determinar AM/PM
        val partesHora = medicamento.hora_toma.split(":")
        val horaString = partesHora[0].trim()
        val minutoString = partesHora[1].trim().substringBefore(" ")
        val ampm = partesHora[1].trim().substringAfter(" ")

        // Convertir a formato de 24 horas
        val horaTomaInt = horaString.toInt()
        val minutoTomaInt = minutoString.toInt()
        val horaToma24Horas = if (ampm.equals("p.m.", ignoreCase = true) && horaTomaInt != 12) {
            horaTomaInt + 12
        } else if (ampm.equals("a.m.", ignoreCase = true) && horaTomaInt == 12) {
            0
        } else {
            horaTomaInt
        }

        viewHolder.horaPill.text = medicamento.hora_toma.split(":").take(2).joinToString(":")

        viewHolder.pillImage.setImageResource(R.drawable.pill)
        viewHolder.checkImage.setImageResource(
            if (medicamento.tomado == 0) R.drawable.check_desabilitado else R.drawable.check_habilitado
        )

        // Obtener la hora actual
        val calendar = Calendar.getInstance()
        val horaActual = calendar.get(Calendar.HOUR_OF_DAY)
        val minutoActual = calendar.get(Calendar.MINUTE)

        // Comparar la hora actual con la hora programada en formato de 24 horas
        val esClickable = medicamento.tomado == 0 && (horaActual > horaToma24Horas || (horaActual == horaToma24Horas && minutoActual >= minutoTomaInt))

        viewHolder.cardView.setOnClickListener {
            if (esClickable) {
                onItemClickListener?.invoke(medicamento.registro_id)
            }
        }
        viewHolder.cardView.isClickable = esClickable
        viewHolder.cardView.isFocusable = esClickable
    }

    override fun getItemCount(): Int {
        return medicamentos.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pillImage: ImageView = itemView.findViewById(R.id.pillIcon)
        var checkImage: ImageView = itemView.findViewById(R.id.checkIcon)
        var nombrePill: TextView = itemView.findViewById(R.id.nombrePastilla)
        var dosisPill: TextView = itemView.findViewById(R.id.dosisPastilla)
        var horaPill: TextView = itemView.findViewById(R.id.horaPastilla)
        val cardView: CardView = itemView.findViewById(R.id.cardMedicamento)
    }

}