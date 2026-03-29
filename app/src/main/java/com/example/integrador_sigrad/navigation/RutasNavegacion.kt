package com.example.integrador_sigrad.navigation

object RutasNavegacion {
    const val INICIO = "inicio"
    const val AREAS = "areas"
    const val HISTORIAL = "historial"
    const val PERFIL = "perfil"
    const val LOGIN = "login"
    const val EDITAR_PERFIL = "editar_perfil"

    const val DETALLE_AREA = "detalle/{nombreCancha}"
    const val FORMULARIO_RESERVA = "formulario_reserva/{nombreCancha}"

    // ✅ Nueva ruta para editar reserva — pasa el ID de la reserva
    const val EDITAR_RESERVA = "editar_reserva/{idReserva}"

    fun detalleArea(nombreCancha: String): String = "detalle/$nombreCancha"
    fun formularioReserva(nombreCancha: String): String = "formulario_reserva/$nombreCancha"

    // ✅ Helper para navegar a editar con el ID real
    fun editarReserva(idReserva: Long): String = "editar_reserva/$idReserva"
}