package com.example.integrador_sigrad.model

import com.google.gson.annotations.SerializedName

data class CarreraResponse(
    @SerializedName("id")
    val id: Long,

    @SerializedName("nombre")
    val nombre: String
)