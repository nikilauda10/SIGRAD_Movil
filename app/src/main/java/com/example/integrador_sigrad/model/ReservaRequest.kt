package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class ReservaRequest(
    val idArea: Long,
    val idUsuario: Long,
    val fecha: String,
    val horaInicio: String,

    @SerializedName("horaFin")
    val horaFin: String,

    val descripcion: String
)