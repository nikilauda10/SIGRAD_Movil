package com.example.integrador_sigrad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.AreaDeportiva
import com.example.integrador_sigrad.model.AreaEnReserva
import com.example.integrador_sigrad.model.Reserva
import com.example.integrador_sigrad.model.ReservaResponse
import com.example.integrador_sigrad.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AreaDeportivaViewModel : ViewModel() {

    private val _areas = MutableStateFlow<List<AreaDeportiva>>(emptyList())
    val areas: StateFlow<List<AreaDeportiva>> = _areas.asStateFlow()

    private val _estaCargando = MutableStateFlow(false)
    val estaCargando: StateFlow<Boolean> = _estaCargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cancelacionExitosa = MutableStateFlow(false)
    val cancelacionExitosa: StateFlow<Boolean> = _cancelacionExitosa.asStateFlow()

    private val _reservasUsuario = MutableStateFlow<List<Reserva>>(emptyList())
    val reservasUsuario: StateFlow<List<Reserva>> = _reservasUsuario.asStateFlow()

    // Guardamos el idUsuario para reusar en recargas
    private var idUsuarioActual: Long = 0L
    private val _triggerRecarga = MutableStateFlow(0)
    val triggerRecarga: StateFlow<Int> = _triggerRecarga.asStateFlow()

    init {
        obtenerAreas()
    }

    /**
     * Carga áreas y reservas del usuario, luego cruza los datos
     * para saber qué área está reservada por este usuario.
     */
    fun cargarTodo(idUsuario: Long) {
        idUsuarioActual = idUsuario
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            try {
                val areasDeferred = async { RetrofitClient.apiService.obtenerAreas() }
                val reservasDeferred = async { RetrofitClient.apiService.obtenerReservasPorUsuario(idUsuario) }

                val responseAreas = areasDeferred.await()
                val responseReservas = reservasDeferred.await()

                val areasRaw = if (responseAreas.isSuccessful) {
                    responseAreas.body() ?: emptyList()
                } else emptyList()

                val reservasConfirmadas = if (responseReservas.isSuccessful) {
                    responseReservas.body()?.filter { it.estado == "CONFIRMADA" } ?: emptyList()
                } else emptyList()

                _reservasUsuario.value =  reservasConfirmadas

                // ✅ Guardamos TODAS las reservas de cada área, no solo la primera
                _areas.value = areasRaw.map { area ->
                    val reservasDeEstaArea = reservasConfirmadas.filter { it.area.id == area.id }
                    area.copy(
                        idUsuarioReserva = if (reservasDeEstaArea.isNotEmpty()) idUsuario else null,
                        reservasActivas = reservasDeEstaArea.map { r ->
                            // Convertimos Reserva a ReservaResponse para mantener compatibilidad
                            ReservaResponse(
                                id = r.id,
                                estado = r.estado,
                                fecha = r.fecha,
                                horaInicio = r.horaInicio,
                                horaFin = r.horaFin,
                                descripcion = r.descripcion,
                                area = AreaEnReserva(id = r.area.id)
                            )
                        }
                    )
                }

            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
                _areas.value = emptyList()
            } finally {
                _estaCargando.value = false
            }
        }
    }

    // Recarga todo usando el idUsuario guardado
    fun obtenerAreas() {
        if (idUsuarioActual != 0L) {
            cargarTodo(idUsuarioActual)
        } else {
            // Si aún no hay usuario, solo cargamos áreas sin cruzar
            viewModelScope.launch {
                _estaCargando.value = true
                try {
                    val response = RetrofitClient.apiService.obtenerAreas()
                    if (response.isSuccessful) {
                        _areas.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al cargar áreas: ${response.code()}"
                    }
                } catch (e: Exception) {
                    _error.value = "Error de conexión: ${e.message}"
                } finally {
                    _estaCargando.value = false
                }
            }
        }
    }

    /**
     * Cancela la reserva usando el idReservaActiva que ya tenemos en el área.
     */
    fun cancelarReservaDeArea(idArea: Long, idUsuario: Long) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            try {
                // Buscamos el ID de reserva directamente en la lista de áreas enriquecida
                val area = _areas.value.find { it.id == idArea }
                val idReserva = area?.idReservaActiva

                if (idReserva == null) {
                    _error.value = "No se encontró una reserva activa para esta área."
                    _estaCargando.value = false
                    return@launch
                }

                val response = RetrofitClient.apiService.cancelarReserva(idReserva)

                if (response.isSuccessful) {
                    _cancelacionExitosa.value = true
                    _triggerRecarga.value += 1  // ✅ Fuerza recarga en la UI
                    cargarTodo(idUsuario)
                } else {
                    _error.value = "Error al cancelar: ${response.code()}"
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

    fun limpiarCancelacion() {
        _cancelacionExitosa.value = false
    }
    fun cancelarReservaEspecifica(idReserva: Long, idUsuario: Long) {
        viewModelScope.launch {
            _estaCargando.value = true
            _error.value = null
            try {
                val response = RetrofitClient.apiService.cancelarReserva(idReserva)
                if (response.isSuccessful) {
                    _cancelacionExitosa.value = true
                    _triggerRecarga.value += 1
                    cargarTodo(idUsuario)
                } else {
                    _error.value = "Error al cancelar: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _estaCargando.value = false
            }
        }
    }
}