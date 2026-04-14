package com.example.integrador_sigrad.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.integrador_sigrad.model.Usuario
import com.google.gson.Gson

class SessionManager(context: Context) {

    // Archivo de preferencias privado para tu app
    private val prefs: SharedPreferences = context.getSharedPreferences("sigrad_sesion", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Guarda el objeto Usuario convirtiéndolo a JSON (texto)
    fun guardarUsuario(usuario: Usuario) {
        val json = gson.toJson(usuario)
        prefs.edit().putString("USUARIO_LOGUEADO", json).apply()
    }

    // Lee el JSON y lo vuelve a convertir en un objeto Usuario
    fun obtenerUsuario(): Usuario? {
        val json = prefs.getString("USUARIO_LOGUEADO", null)
        return if (json != null) {
            gson.fromJson(json, Usuario::class.java)
        } else {
            null
        }
    }

    // Borra la sesión cuando el usuario le da a "Cerrar Sesión"
    fun limpiarSesion() {
        prefs.edit().clear().apply()
    }
}