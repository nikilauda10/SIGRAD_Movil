package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class AreaDeportiva(
    val id: Long,
    val nombre: String,
    val ubicacion: String,
    val horaApertura: String,
    val horaCierre: String,
    val imagen: String?,
    val estado: String = "DISPONIBLE",
    // Este campo NO viene del JSON, lo calculamos nosotros en el ViewModel
    val idUsuarioReserva: Long? = null,
    // Guardamos el ID de la reserva para poder cancelarla o editarla
    val idReservaActiva: Long? = null,
    val reservasActivas: List<ReservaResponse> = emptyList()
)