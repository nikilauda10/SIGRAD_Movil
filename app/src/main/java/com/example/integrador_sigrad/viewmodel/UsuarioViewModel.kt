package com.example.integrador_sigrad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.Usuario
import com.example.integrador_sigrad.network.RetrofitClient
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    var estaActualizando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var exitoActualizacion by mutableStateOf(false)

    fun actualizarDatosPerfil(usuarioActualizado: Usuario, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            estaActualizando = true
            mensajeError = null
            exitoActualizacion = false

            try {
                val id = usuarioActualizado.id
                if (id == null) {
                    mensajeError = "No se pudo identificar al usuario."
                    onResultado(false)
                    return@launch
                }

                val response = RetrofitClient.apiService.actualizarUsuario(id, usuarioActualizado)

                if (response.isSuccessful) {
                    exitoActualizacion = true
                    onResultado(true)
                } else {
                    val error = response.errorBody()?.string() ?: "Error desconocido"
                    mensajeError = "Error al actualizar: $error"
                    onResultado(false)
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
                onResultado(false)
            } finally {
                estaActualizando = false
            }
        }
    }

    fun limpiarEstados() {
        mensajeError = null
        exitoActualizacion = false
    }
}