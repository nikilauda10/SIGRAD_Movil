package com.example.integrador_sigrad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.ReservaRequest
import com.example.integrador_sigrad.network.RetrofitClient // <-- Importante: Importa tu RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReservaViewModel : ViewModel() {

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    private val _reservaExitosa = MutableStateFlow(false)
    val reservaExitosa: StateFlow<Boolean> = _reservaExitosa

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun crearReserva(request: ReservaRequest) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null

            try {
                println("DATOS ENVIADOS AL BACKEND: $request")
                // AQUÍ ESTÁ LA MAGIA DE VERDAD (Llamada al backend)
                val response = RetrofitClient.apiService.crearReserva(request)

                if (response.isSuccessful) {
                    _reservaExitosa.value = true
                } else {
                    // 👇 ESTO SACARÁ EL ERROR REAL DE SPRING BOOT
                    val codigoError = response.code()
                    val mensajeReal = response.errorBody()?.string() ?: "Sin detalles"

                    println("🚨 CÓDIGO DE ERROR: $codigoError")
                    println("🚨 MENSAJE REAL: $mensajeReal")

                    // Va a imprimir el error exacto en las letras rojas de tu celular
                    _error.value = "Error $codigoError: $mensajeReal"
                    _reservaExitosa.value = false
                }

            } catch (e: Exception) {
                // Si el servidor está apagado o no hay internet
                _error.value = "Error al conectar con el servidor: ${e.message}"
                _reservaExitosa.value = false
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun limpiarEstado() {
        _reservaExitosa.value = false
        _error.value = null
    }

    fun actualizarReserva(
        idReserva: Long,
        idArea: Long,
        idUsuario: Long,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        descripcion: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            try {
                val request = ReservaRequest(
                    idArea = idArea,
                    idUsuario = idUsuario,
                    fecha = fecha,
                    horaInicio = horaInicio,
                    horaFin = horaFin,
                    descripcion = descripcion
                )
                val response = RetrofitClient.apiService.actualizarReserva(idReserva, request)

                if (response.isSuccessful) {
                    onExito() // ✅ Navega de regreso a áreas
                } else {
                    val mensajeReal = response.errorBody()?.string() ?: "Sin detalles"
                    _error.value = "Error al actualizar: $mensajeReal"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _estaCargando.value = false
            }
        }
    }
}