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

    // Estado para saber si estamos guardando cambios en el servidor
    var estaActualizando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)
    var exitoActualizacion by mutableStateOf(false)

    /**
     * Función para actualizar los datos editables.
     * Recibe el objeto usuario completo pero solo con los campos teléfono y carrera cambiados.
     */
    fun actualizarDatosPerfil(usuarioActualizado: Usuario, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            estaActualizando = true
            mensajeError = null
            exitoActualizacion = false

            try {
                // Aquí llamamos al endpoint de tu amigo.
                // Por ahora, como no está listo, simulamos una respuesta exitosa.
                // val response = RetrofitClient.apiService.actualizarUsuario(usuarioActualizado)

                // --- SIMULACIÓN DE ESPERA DEL BACKEND ---
                kotlinx.coroutines.delay(1000)
                val esExitoso = true // Cambiar a response.isSuccessful cuando esté la API
                // ---------------------------------------

                if (esExitoso) {
                    exitoActualizacion = true
                    onResultado(true)
                } else {
                    mensajeError = "Error al actualizar los datos en el servidor."
                    onResultado(false)
                }
            } catch (e: Exception) {
                mensajeError = "Error de red: No se pudo conectar con el servidor."
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