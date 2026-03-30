package com.example.integrador_sigrad.network

import com.example.integrador_sigrad.model.AreaDeportiva
import com.example.integrador_sigrad.model.ReservaRequest
import com.example.integrador_sigrad.model.ReservaResponse
import com.example.integrador_sigrad.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


data class LoginRequest(val correo: String, val password: String)
data class AuthResponse(
    val status: String? = null,
    val message: String? = null,
    val id: Long? = null,
    val nombre: String? = null,
    val rol: String? = null,
    val emailInstitucional: String? = null
)

interface ApiService {

    // 1. Iniciar Sesión (Ruta correcta)
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    // 👇 2. Registrar Usuario (¡Ruta corregida! Antes decía auth/registro)
    @POST("api/usuarios/registrar")
    suspend fun registrarUsuario(@Body usuario: Usuario): Response<AuthResponse>

    // 3. Ruta para obtener la lista de Áreas Deportivas
    @GET("api/areas/listar")
    suspend fun obtenerAreas(): Response<List<AreaDeportiva>>

    // 👇 4. ¡NUEVA RUTA! Crear Reserva
    @POST("api/reservas/crear") // <-- REVISA QUE SE LLAME ASÍ EN TU SPRING BOOT
    suspend fun crearReserva(@Body request: ReservaRequest): Response<Void>

    // En tu ApiService.kt — agrega este endpoint
    // Cancelar reserva por ID de reserva
    @PUT("api/reservas/cancelar/{id}")
    suspend fun cancelarReserva(@Path("id") idReserva: Long): Response<Map<String, Any>>

    // Obtener reservas de un usuario (para saber el ID de reserva de cada área)
    @GET("api/reservas/usuario/{idUsuario}")
    suspend fun obtenerReservasPorUsuario(@Path("idUsuario") idUsuario: Long): Response<List<ReservaResponse>>

    @PUT("api/reservas/actualizar/{id}")
    suspend fun actualizarReserva(
        @Path("id") idReserva: Long,
        @Body request: ReservaRequest
    ): Response<Map<String, Any>>

    @GET("api/reservas/ocupadas/{idArea}/{fecha}")
    suspend fun obtenerHorasOcupadas(
        @Path("idArea") idArea: Long,
        @Path("fecha") fecha: String
    ): Response<List<ReservaResponse>>


    //Perfil (Editarlo y traer la info )

    @PUT("api/usuarios/actualizar/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body usuario: Usuario
    ): Response<Map<String, Any>>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuarioPorId(@Path("id") id: Long): Response<Usuario>
}