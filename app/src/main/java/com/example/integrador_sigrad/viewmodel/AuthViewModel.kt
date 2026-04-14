package com.example.integrador_sigrad.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.integrador_sigrad.model.Usuario
import com.example.integrador_sigrad.network.LoginRequest
import com.example.integrador_sigrad.network.RetrofitClient
import com.example.integrador_sigrad.utils.SessionManager // Importamos la nueva clase
import kotlinx.coroutines.launch

// ✅ CAMBIO 1: Cambiamos a AndroidViewModel para poder usar el Contexto de la app
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var mostrarDialogoExito by mutableStateOf(false)
    var usuarioLogueado by mutableStateOf<Usuario?>(null)

    var listaRoles by mutableStateOf<List<String>>(listOf("ESTUDIANTE", "DOCENTE"))
    var listaCarreras by mutableStateOf<List<String>>(emptyList())

    // ✅ CAMBIO 2: Inicializamos el SessionManager
    private val sessionManager = SessionManager(application)

    init {
        cargarCatalogos()
        verificarSesionGuardada() // Revisamos si hay alguien logueado al abrir la app
    }

    // ✅ CAMBIO 3: Función que lee la memoria al abrir la app
    private fun verificarSesionGuardada() {
        val usuarioGuardado = sessionManager.obtenerUsuario()
        if (usuarioGuardado != null) {
            usuarioLogueado = usuarioGuardado
        }
    }

    private fun cargarCatalogos() {
        viewModelScope.launch {
            try {
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

                    usuarioLogueado = Usuario(
                        id = body?.id,
                        nombre = body?.nombre ?: "",
                        emailInstitucional = body?.emailInstitucional ?: correo,
                        rol = body?.rol ?: "",
                        matricula = body?.matricula ?: "",
                        carrera = body?.carrera ?: "",
                        telefono = body?.telefono ?: "",
                        contrasena = contrasena
                    )

                    // ✅ CAMBIO 4: Guardamos físicamente la sesión
                    usuarioLogueado?.let { sessionManager.guardarUsuario(it) }

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
        // ... (Tu código de registro se queda exactamente igual) ...
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
        // ✅ CAMBIO 5: Actualizamos la sesión guardada si el usuario edita su perfil
        usuarioLogueado?.let { sessionManager.guardarUsuario(it) }
    }

    fun cerrarSesion() {
        usuarioLogueado = null
        errorMessage = null
        mostrarDialogoExito = false
        // ✅ CAMBIO 6: Borramos los datos del teléfono
        sessionManager.limpiarSesion()
    }
}