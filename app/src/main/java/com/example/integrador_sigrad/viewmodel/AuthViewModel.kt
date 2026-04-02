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
                    val idUsuario = body?.id ?: 0L

                    println("✅ LOGIN OK: id=$idUsuario nombre=${body?.nombre}")

                    // ✅ Cargamos el usuario COMPLETO con todos sus datos
                    try {
                        val responseUsuario = RetrofitClient.apiService.obtenerUsuarioPorId(idUsuario)
                        println("📡 CÓDIGO RESPUESTA USUARIO: ${responseUsuario.code()}")
                        println("📡 BODY USUARIO: ${responseUsuario.body()}")
                        println("📡 ERROR USUARIO: ${responseUsuario.errorBody()?.string()}")
                        if (responseUsuario.isSuccessful) {
                            usuarioLogueado = responseUsuario.body()
                            println("✅ USUARIO COMPLETO: ${usuarioLogueado}")
                        } else {
                            // Si falla la segunda llamada, guardamos lo poco que tenemos del login
                            usuarioLogueado = Usuario(
                                id = idUsuario,
                                nombre = body?.nombre ?: "",
                                emailInstitucional = correo,
                                rol = body?.rol ?: "",
                                matricula = "",
                                carrera = "",
                                telefono = "",
                                contrasena = ""
                            )
                            println("⚠️ No se pudo cargar usuario completo, usando datos básicos")
                        }
                    } catch (e: Exception) {
                        // Si hay error de red en la segunda llamada, usamos datos básicos
                        usuarioLogueado = Usuario(
                            id = idUsuario,
                            nombre = body?.nombre ?: "",
                            emailInstitucional = correo,
                            rol = body?.rol ?: "",
                            matricula = "",
                            carrera = "",
                            telefono = "",
                            contrasena = ""
                        )
                        println("⚠️ Error cargando usuario completo: ${e.message}")
                    }

                    onSuccess()
                    println("🔍 ID USUARIO PARA BUSCAR: $idUsuario")
                    println("🔍 USUARIO LOGUEADO ACTUAL: $usuarioLogueado")
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

    // ✅ Actualiza el usuario logueado localmente después de editar perfil
    // Para que los cambios se vean inmediatamente en PerfilScreen sin re-login
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