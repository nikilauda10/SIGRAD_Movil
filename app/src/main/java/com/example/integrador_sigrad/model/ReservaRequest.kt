package com.example.integrador_sigrad.model

data class ReservaRequest(
    val idArea: Long,
    val idUsuario: Long,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val descripcion: String // <-- CAMBIADO A DESCRIPCION
)