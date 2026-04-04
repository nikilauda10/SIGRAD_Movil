package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Long? = null,
    val nombre: String,
    val matricula: String,
    val telefono: String,
    val carrera: String,

    @SerializedName("emailInstitucional")
    val emailInstitucional: String, // <-- ¡OJO! Ahora lleva la "I" mayúscula

    val contrasena: String,
    val rol: String = "ALUMNO",
    val estado: Boolean = true,
    val validado: Boolean = false
)