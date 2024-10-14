package com.example.pillpop

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.text.TextPaint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pillpop.databinding.FragmentProgresoBinding
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProgresoFragment : Fragment() {
    private lateinit var binding: FragmentProgresoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgresoBinding.inflate(inflater, container, false)
        val view = binding.root

        val spinnerFrecuenciaTiempo: Spinner = view.findViewById(R.id.FrecuenciaReporteDrop)
        val frecuenciaTiempoList = resources.getStringArray(R.array.frecuencia_reporte_array)
        val adapterFrecuenciaTiempo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, frecuenciaTiempoList)
        spinnerFrecuenciaTiempo.adapter = adapterFrecuenciaTiempo

        val linearEntreFechas: LinearLayout = view.findViewById(R.id.LinearEntreFechas)
        val linearFechaUnica: LinearLayout = view.findViewById(R.id.LinearfechaUnica)

        // Establecer el listener para el Spinner
        spinnerFrecuenciaTiempo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                when (selectedItem) {
                    "Seleccionar..." -> {
                        linearEntreFechas.visibility = View.GONE
                        linearFechaUnica.visibility = View.GONE
                    }
                    "Diario" -> {
                        linearEntreFechas.visibility = View.GONE
                        linearFechaUnica.visibility = View.VISIBLE
                    }
                    "Entre Fechas" -> {
                        linearEntreFechas.visibility = View.VISIBLE
                        linearFechaUnica.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada
            }
        }

        val calendario = Calendar.getInstance()
        val fecha = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, day)

            actualizarFecha(calendario)
        }

        binding.fechaPickBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                fecha,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = calendario.timeInMillis
            datePickerDialog.show()
        }

        val calendarioInicio = Calendar.getInstance()
        val fechaInicio = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendarioInicio.set(Calendar.YEAR, year)
            calendarioInicio.set(Calendar.MONTH, month)
            calendarioInicio.set(Calendar.DAY_OF_MONTH, day)

            actualizarFechaInicio(calendarioInicio)
        }

        binding.fechaPickBtnInicio.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                fechaInicio,
                calendarioInicio.get(Calendar.YEAR),
                calendarioInicio.get(Calendar.MONTH),
                calendarioInicio.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = calendarioInicio.timeInMillis
            datePickerDialog.show()
        }

        val calendarioFin = Calendar.getInstance()
        val fechaFin = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendarioFin.set(Calendar.YEAR, year)
            calendarioFin.set(Calendar.MONTH, month)
            calendarioFin.set(Calendar.DAY_OF_MONTH, day)

            actualizarFechaFin(calendarioFin)
        }

        binding.fechaPickBtnFin.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireActivity(),
                fechaFin,
                calendarioFin.get(Calendar.YEAR),
                calendarioFin.get(Calendar.MONTH),
                calendarioFin.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = calendarioInicio.timeInMillis
            datePickerDialog.show()
        }

        // Vincular el botón de descarga de PDF
        val descargarBtn = view.findViewById<Button>(R.id.Descargar_btn)

        descargarBtn.setOnClickListener {
            //obtenerDatosReporte()
            abrirSelectorDeArchivos()
        }

        return view
    }

    private fun abrirSelectorDeArchivos() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "Reporte de Progreso.pdf")
        }
        startActivityForResult(intent, REQUEST_CODE_CREATE_DOCUMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CREATE_DOCUMENT && resultCode == AppCompatActivity.RESULT_OK) {
            data?.data?.let { uri ->
                generarPdf(
                    uri,
                    "Reporte de Progreso",
                    "Este documento contiene el seguimiento del tratamiento médico."
                )
            }
        }
    }

    fun generarPdf(uri: Uri, tituloText: String, descripcionText: String) {
        val pdfDocument = PdfDocument()
        val paint = Paint()
        val titulo = TextPaint()
        val subtitulo = TextPaint()
        val descripcion = TextPaint()
        val tablePaint = Paint()
        val tableTextPaint = TextPaint()

        var paginaNumber = 1
        var paginaInfo: PdfDocument.PageInfo
        lateinit var pagina: PdfDocument.Page
        lateinit var canvas: Canvas

        fun startNewPage() {
            paginaInfo = PdfDocument.PageInfo.Builder(816, 1054, paginaNumber).create()
            pagina = pdfDocument.startPage(paginaInfo)
            canvas = pagina.canvas
            canvas.drawColor(Color.WHITE) // Fondo blanco
            paginaNumber++
        }

        startNewPage() // Inicializa la primera página

        // Título
        titulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        titulo.textSize = 24f
        paint.color = Color.BLACK
        canvas.drawText(tituloText, 10f, 50f, titulo)

        // Línea debajo del título
        paint.strokeWidth = 3f
        canvas.drawLine(10f, 70f, 806f, 70f, paint)

        // Descripción
        descripcion.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL))
        descripcion.textSize = 16f
        descripcion.color = Color.BLACK

        val arrDescripcion = descripcionText.split("\n")

        var y = 100f
        for (item in arrDescripcion) {
            // Verifica si se necesita una nueva página
            if (y > 1054 - 20) { // Si se pasa de la altura de la página menos un margen
                pdfDocument.finishPage(pagina) // Termina la página actual
                startNewPage() // Crea una nueva página
                y = 100f // Reinicia la posición vertical
            }
            canvas.drawText(item, 10f, y, descripcion)
            y += 20
        }

        // Espacio entre la descripción y la tabla
        y += 20f

        // Información del mes, doctor y fecha
        val mes = "Mes: Junio"
        val doctor = "Paciente: José Perez Cabrera"
        val fecha = "Fecha: 14 de octubre del 2024"

        // Títulos y texto normal
        val infoBoldPaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 16f
            color = Color.BLACK
        }

        val infoNormalPaint = TextPaint().apply {
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
            textSize = 16f
            color = Color.BLACK
        }

        // Título
        subtitulo.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        subtitulo.textSize = 20f

        canvas.drawText("Datos de Reporte:", 10f, y, subtitulo)
        y += 40f

        canvas.drawText("Mes:", 10f, y, infoBoldPaint)
        canvas.drawText("Junio", 100f, y, infoNormalPaint) // Añadiendo el valor en normal
        y += 20f
        canvas.drawText("Paciente:", 10f, y, infoBoldPaint)
        canvas.drawText("José Perez Cabrera", 100f, y, infoNormalPaint) // Añadiendo el valor en normal
        y += 20f
        canvas.drawText("Fecha:", 10f, y, infoBoldPaint)
        canvas.drawText("14 de octubre del 2024", 100f, y, infoNormalPaint) // Añadiendo el valor en normal

        // Espacio entre la información y la tabla
        y += 40f

        // Tabla de Tratamiento
        canvas.drawText("Tratamiento:", 10f, y, subtitulo)
        y += 20f

        // Inicia la tabla
        tablePaint.color = Color.LTGRAY
        canvas.drawRect(10f, y, 806f, y + 20f, tablePaint)

        // Títulos de la tabla
        canvas.drawText("Tratamiento", 20f, y + 15f, tableTextPaint)
        canvas.drawText("Dosis", 200f, y + 15f, tableTextPaint)
        canvas.drawText("Duración", 320f, y + 15f, tableTextPaint)
        canvas.drawText("Frecuencia", 440f, y + 15f, tableTextPaint)
        canvas.drawText("Doctor", 620f, y + 15f, tableTextPaint)

        // Datos de la tabla
        val tratamientos = arrayOf(
            arrayOf("Pastilla 1", "100gr", "2 semanas", "Diaria", "Mauricio Reyes"),
            arrayOf(
                "Pastilla 2",
                "100gr",
                "1 mes",
                "Dos días seguidos, dejando un día",
                "Mauricio Reyes"
            ),
            arrayOf("Pastilla 3", "100gr", "1 mes", "Interdiario", "Mauricio Reyes")
        )

        y += 40f
        for (tratamiento in tratamientos) {
            if (y > 1054 - 20) {
                pdfDocument.finishPage(pagina)
                startNewPage()
                y = 100f
            }
            drawMultilineText(canvas, tratamiento[0], 20f, y, tableTextPaint, 180f)
            drawMultilineText(canvas, tratamiento[1], 200f, y, tableTextPaint, 80f)
            drawMultilineText(canvas, tratamiento[2], 320f, y, tableTextPaint, 80f)
            drawMultilineText(canvas, tratamiento[3], 440f, y, tableTextPaint, 140f)
            drawMultilineText(canvas, tratamiento[4], 620f, y, tableTextPaint, 100f)
            y += 50f
        }

        // Espacio antes del Registro Diario
        y += 30f

        // Registro Diario
        canvas.drawText("Registro Diario:", 10f, y, subtitulo)
        y += 20f

        // Crear tabla de registro diario
        val registroTitulos = arrayOf("Fecha", "Pastilla 1", "Pastilla 2", "Pastilla 3")
        val registros = arrayOf(
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔"),
            arrayOf("01/07/2024", "✔", "✖", "✔"),
            arrayOf("02/07/2024", "✔", "✔", "✖"),
            arrayOf("03/07/2024", "✖", "✖", "✔")
        )

        // Dibujar títulos de la tabla
        tablePaint.color = Color.LTGRAY
        canvas.drawRect(10f, y, 806f, y + 20f, tablePaint)

        for (i in registroTitulos.indices) {
            canvas.drawText(registroTitulos[i], 20f + i * 120f, y + 15f, tableTextPaint)
        }

        // Espacio para los registros
        y += 30f

        for (registro in registros) {
            for (i in registro.indices) {
                if (y > 1054 - 20) {
                    pdfDocument.finishPage(pagina)
                    startNewPage()
                    y = 100f
                }
                canvas.drawText(registro[i], 20f + i * 120f, y + 15f, tableTextPaint)
            }
            y += 30f
        }

        pdfDocument.finishPage(pagina)

        // Guardar el PDF en el URI proporcionado
        try {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(uri)
            outputStream?.use {
                pdfDocument.writeTo(it)
                Toast.makeText(requireContext(), "Se creó el PDF correctamente", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error al crear el PDF", Toast.LENGTH_LONG).show()
        }

        pdfDocument.close()
    }

    fun drawMultilineText(canvas: Canvas, text: String, x: Float, y: Float, paint: TextPaint, maxWidth: Float) {
        val words = text.split(" ")
        var line = ""
        var lineHeight = paint.descent() - paint.ascent()
        var cellY = y

        for (word in words) {
            val testLine = "$line $word".trim()
            val testWidth = paint.measureText(testLine)

            if (testWidth > maxWidth) {
                canvas.drawText(line, x, cellY, paint)
                line = word // Iniciar nueva línea con la palabra actual
                cellY += lineHeight // Aumentar la altura para la próxima línea
            } else {
                line = testLine
            }
        }
        canvas.drawText(line, x, cellY, paint) // Dibuja la última línea
    }


    // Funciones para actualizar campos de fecha
    private fun actualizarFecha(calendar: Calendar) {
        val formatoFecha = "dd/MM/yyyy"
        val formatoSimple = SimpleDateFormat(formatoFecha, Locale("es", "ES"))
        binding.editTextDateUnico.setText(formatoSimple.format(calendar.time))
    }

    private fun actualizarFechaInicio(calendar: Calendar) {
        val formatoFecha = "dd/MM/yyyy"
        val formatoSimple = SimpleDateFormat(formatoFecha, Locale("es", "ES"))
        binding.editTextDateInicio.setText(formatoSimple.format(calendar.time))
    }

    private fun actualizarFechaFin(calendar: Calendar) {
        val formatoFecha = "dd/MM/yyyy"
        val formatoSimple = SimpleDateFormat(formatoFecha, Locale("es", "ES"))
        binding.editTextDateFin.setText(formatoSimple.format(calendar.time))
    }

    companion object {
        private const val REQUEST_CODE_CREATE_DOCUMENT = 1
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProgresoFragment().apply {
                arguments = Bundle().apply {
                    // Aquí puedes agregar los parámetros si es necesario
                }
            }
    }
}