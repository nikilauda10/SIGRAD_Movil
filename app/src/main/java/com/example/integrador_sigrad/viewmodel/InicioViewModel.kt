package com.example.integrador_sigrad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.Reserva // ✅ Importamos Reserva
import com.example.integrador_sigrad.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InicioViewModel : ViewModel() {

    // ✅ Usamos Reserva en lugar de ReservaResponse
    private val _reservasActivas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservasActivas: StateFlow<List<Reserva>> = _reservasActivas.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarReservasDelUsuario(idUsuario: Long) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            try {
                // ✅ CAMBIO CLAVE: Usamos obtenerReservasPorUsuario
                val respuesta = RetrofitClient.apiService.obtenerReservasPorUsuario(idUsuario)

                if (respuesta.isSuccessful) {
                    // Le decimos explícitamente a Kotlin que es una lista de Reserva
                    val listaReservas = respuesta.body() ?: emptyList<Reserva>()

                    val soloActivas = listaReservas.filter { reserva ->
                        reserva.estado.uppercase() == "CONFIRMADA"
                    }

                    _reservasActivas.value = soloActivas
                } else {
                    _error.value = "No se pudieron obtener las reservas."
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _estaCargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}