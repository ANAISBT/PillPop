package com.example.pillpop

data class DatosReporteResponse(
    val datosReporte: List<Reporte>,
    val tratamiento: List<Tratamiento>,
    val tomasDiarias: List<TomaDiaria>
)