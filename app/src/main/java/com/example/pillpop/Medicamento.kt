package com.example.pillpop

data class Medicamento(
    val registro_id: Int,
    val medicamento_nombre: String,
    val dosis: Int,
    val paciente_id: Int,
    val fecha_toma: String,
    val hora_toma: String,
    val tomado: Int
)