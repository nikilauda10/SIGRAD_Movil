package com.example.integrador_sigrad.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.Usuario
import com.example.integrador_sigrad.network.LoginRequest
import com.example.integrador_sigrad.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var mostrarDialogoExito by mutableStateOf(false)
    var usuarioLogueado by mutableStateOf<Usuario?>(null)

    // 1. ROLES FIJOS (Solo Estudiante y Docente para la app móvil)
    var listaRoles by mutableStateOf<List<String>>(listOf("ESTUDIANTE", "DOCENTE"))
    var listaCarreras by mutableStateOf<List<String>>(emptyList())

    init {
        cargarCatalogos()
    }

    private fun cargarCatalogos() {
        viewModelScope.launch {
            try {
                // Ya NO descargamos roles del backend, usamos la lista fija de arriba.
                // Solo descargamos las carreras.
                val resCarreras = RetrofitClient.apiService.obtenerCarreras()
                if (resCarreras.isSuccessful) {
                    listaCarreras = resCarreras.body()?.map { it.nombre } ?: emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(correo: String, contrasena: String, onSuccess: () -> Unit) {
        if (correo.isBlank() || contrasena.isBlank()) {
            errorMessage = "Por favor, llena todos los campos."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = LoginRequest(correo = correo, password = contrasena)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val body = response.body()

                    // 2. AQUÍ ATRAPAMOS LA MATRÍCULA Y TODOS LOS DATOS DESDE EL LOGIN
                    usuarioLogueado = Usuario(
                        id = body?.id,
                        nombre = body?.nombre ?: "",
                        emailInstitucional = body?.emailInstitucional ?: correo,
                        rol = body?.rol ?: "",
                        matricula = body?.matricula ?: "", // <-- ESTO ES LO QUE TE FALTABA
                        carrera = body?.carrera ?: "",
                        telefono = body?.telefono ?: "",
                        contrasena = contrasena
                    )
                    onSuccess()
                } else {
                    val codigoError = response.code()
                    val mensajeBackend = response.errorBody()?.string() ?: "Sin mensaje"

                    var mensajeLimpio = "Error $codigoError"
                    try {
                        val jsonError = org.json.JSONObject(mensajeBackend)
                        mensajeLimpio = jsonError.getString("mensaje")
                    } catch (e: Exception) {
                        mensajeLimpio = mensajeBackend
                    }
                    errorMessage = mensajeLimpio
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión. Revisa tu internet o la IP."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun registrar(usuario: Usuario) {
        if (usuario.nombre.isBlank() || usuario.matricula.isBlank() ||
            usuario.emailInstitucional.isBlank() || usuario.carrera.isBlank() ||
            usuario.rol.isBlank() || usuario.contrasena.isBlank() || usuario.telefono.isBlank()
        ) {
            errorMessage = "Por favor, llena todos los campos."
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.apiService.registrarUsuario(usuario)

                if (response.isSuccessful) {
                    mostrarDialogoExito = true
                } else {
                    val errorString = response.errorBody()?.string()
                    var mensajeLimpio = "Error al registrar. Verifica los datos."

                    if (errorString != null) {
                        try {
                            val jsonError = org.json.JSONObject(errorString)
                            mensajeLimpio = jsonError.getString("mensaje")
                        } catch (e: Exception) {
                            mensajeLimpio = errorString
                        }
                    }
                    errorMessage = mensajeLimpio
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión. Revisa tu internet o la IP."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun actualizarUsuarioLocal(nuevoTelefono: String, nuevaCarrera: String) {
        usuarioLogueado = usuarioLogueado?.copy(
            telefono = nuevoTelefono,
            carrera = nuevaCarrera
        )
    }

    fun cerrarSesion() {
        usuarioLogueado = null
        errorMessage = null
        mostrarDialogoExito = false
    }
}