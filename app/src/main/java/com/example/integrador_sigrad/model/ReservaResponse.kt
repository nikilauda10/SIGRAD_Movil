package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class ReservaResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("estado")
    val estado: String,

    @SerializedName("fecha")
    val fecha: String? = null,

    @SerializedName("horaInicio")
    val horaInicio: String? = null,

    @SerializedName("horaFin")
    val horaFin: String? = null,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("area")
    val area: AreaEnReserva?
)

data class AreaEnReserva(
    @SerializedName("id")
    val id: Long
)