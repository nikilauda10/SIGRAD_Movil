package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class RolResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("activo")
    val activo: Boolean = true
)