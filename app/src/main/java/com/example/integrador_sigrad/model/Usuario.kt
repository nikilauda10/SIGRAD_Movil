package com.example.integrador_sigrad.model

data class Usuario(
    val id: Long? = null, // Puede ser null cuando te registras
    val nombre: String,
    val matricula: String,
    val telefono: String,
    val carrera: String,
    val emailInstitucional: String,
    val contrasena: String,
    val rol: String = "ALUMNO",
    val estado: Boolean = true,
    val validado: Boolean = false
)
