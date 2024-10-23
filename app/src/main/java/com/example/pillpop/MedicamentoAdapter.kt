package com.example.pillpop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.cardview.widget.CardView

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
        viewHolder.dosisPill.text = "${medicamento.dosis} unidades" // Ajusta según sea necesario
        viewHolder.horaPill.text = medicamento.hora_toma.split(":").take(2).joinToString(":")
        viewHolder.pillImage.setImageResource(R.drawable.pill) // Ajusta si tienes imágenes específicas

        // Cambia el ícono según el estado de tomado
        viewHolder.checkImage.setImageResource(
            if (medicamento.tomado == 0) R.drawable.check_desabilitado
            else R.drawable.check_habilitado
        )

        // Configura el click listener en el CardView
        viewHolder.cardView.setOnClickListener {
            if (medicamento.tomado == 0) {
                onItemClickListener?.invoke(medicamento.registro_id) // Envía el ID del medicamento
            }
        }

        viewHolder.cardView.isClickable = (medicamento.tomado == 0)
        viewHolder.cardView.isFocusable = (medicamento.tomado == 0)
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