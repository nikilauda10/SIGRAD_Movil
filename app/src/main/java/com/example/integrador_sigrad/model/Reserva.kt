package com.example.integrador_sigrad.model

data class AreaSimplificada(
    val id: Long,
    val nombre: String
)

data class Reserva(
    val id: Long,
    val area: AreaSimplificada, // Aquí recibimos el objeto del área deportiva
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String,
    val descripcion: String? // El "?" significa que puede ser nulo (null)
)
