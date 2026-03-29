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

                    // ✅ Guardamos el usuario con el ID real que devuelve el backend
                    usuarioLogueado = Usuario(
                        id = body?.id ?: 0L,
                        nombre = body?.nombre ?: "",
                        emailInstitucional = correo,
                        rol = body?.rol ?: "",
                        matricula = "",
                        carrera = "",
                        telefono = "",
                        contrasena = ""
                    )

                    println("✅ USUARIO LOGUEADO: id=${usuarioLogueado?.id} nombre=${usuarioLogueado?.nombre}")
                    onSuccess()
                } else {
                    val codigoError = response.code()
                    val mensajeBackend = response.errorBody()?.string() ?: "Sin mensaje"
                    println("🚨 ERROR LOGIN: Código $codigoError - Detalles: $mensajeBackend")
                    errorMessage = "Error $codigoError: $mensajeBackend"
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
        if (usuario.nombre.isBlank() || usuario.matricula.isBlank() || usuario.emailInstitucional.isBlank() ||
            usuario.carrera.isBlank() || usuario.rol.isBlank() || usuario.contrasena.isBlank() || usuario.telefono.isBlank()
        ) {
            errorMessage = "Por favor, llena todos los campos, incluyendo el teléfono."
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
                    errorMessage = "Error al registrar. Verifica que el correo sea @utez.edu.mx y no esté repetido."
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión. Revisa tu internet o la IP."
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}