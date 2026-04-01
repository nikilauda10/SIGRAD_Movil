package com.example.integrador_sigrad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.Reserva
import com.example.integrador_sigrad.network.RetrofitClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistorialViewModel : ViewModel() {
    private val _reservas = MutableStateFlow<List<Reserva>>(emptyList())
    val reservas: StateFlow<List<Reserva>> = _reservas

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando

    fun obtenerHistorial(idUsuario: Long) {
        if (idUsuario == 0L) return

        viewModelScope.launch {
            _estaCargando.value = true
            try {
                // AQUÍ LLAMARÍAS A TU API REAL:
                val response = RetrofitClient.apiService.listarReservasPorUsuario(idUsuario)
                _reservas.value = response

            } catch (e: Exception) {
                _reservas.value = emptyList()
            } finally {
                _estaCargando.value = false
            }
        }
    }
}