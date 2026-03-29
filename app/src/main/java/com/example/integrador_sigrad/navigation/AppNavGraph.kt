package com.example.integrador_sigrad.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.integrador_sigrad.ui.screens.*
import com.example.integrador_sigrad.viewmodel.AreaDeportivaViewModel
import com.example.integrador_sigrad.viewmodel.AuthViewModel
import com.example.integrador_sigrad.viewmodel.UsuarioViewModel
import com.example.integrador_sigrad.viewmodel.ReservaViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = RutasNavegacion.INICIO
) {
    val areasViewModel: AreaDeportivaViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val reservaViewModel: ReservaViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- RUTA 1: INICIO ---
        composable(RutasNavegacion.INICIO) {
            InicioScreen(
                onIrAAreas = {
                    navController.navigate(RutasNavegacion.AREAS) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onIrAHistorial = {
                    navController.navigate(RutasNavegacion.HISTORIAL) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        // --- RUTA 2: LISTADO DE ÁREAS ---
        composable(RutasNavegacion.AREAS) {
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L
            val backStackEntry = navController.currentBackStackEntryAsState()

            val triggerRecarga by areasViewModel.triggerRecarga.collectAsState()

            LaunchedEffect(backStackEntry.value?.destination?.route, triggerRecarga) {
                if (backStackEntry.value?.destination?.route == RutasNavegacion.AREAS) {
                    areasViewModel.cargarTodo(idUsuario)
                }
            }

            AreasScreen(
                viewModel = areasViewModel,
                idUsuarioActual = idUsuario,
                onReservarClick = { area ->
                    navController.navigate(RutasNavegacion.formularioReserva(area.nombre))
                },
                onEditarClick = { area ->
                    // ✅ Buscamos el ID de reserva activa de esta área y navegamos con él
                    val idReserva = area.idReservaActiva
                    if (idReserva != null) {
                        navController.navigate(RutasNavegacion.editarReserva(idReserva))
                    }
                },
                onCancelarClick = { area ->
                    // ✅ Cancela usando el ID de reserva activa
                    areasViewModel.cancelarReservaDeArea(area.id, idUsuario)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA 2.1: DETALLE DEL ÁREA ---
        composable(RutasNavegacion.DETALLE_AREA) { backStackEntry ->
            val nombreCancha = backStackEntry.arguments?.getString("nombreCancha") ?: "Área"
            val listaDeAreas by areasViewModel.areas.collectAsState()
            val canchaDeLaBD = listaDeAreas.find { it.nombre == nombreCancha }

            val textoUbicacion = if (canchaDeLaBD != null && canchaDeLaBD.ubicacion.isNotBlank()) {
                "📍 Ubicación: ${canchaDeLaBD.ubicacion}"
            } else {
                "Ubicación no disponible."
            }

            AreaDetalleScreen(
                nombreCancha = nombreCancha,
                descripcionCancha = textoUbicacion,
                imagenUrl = canchaDeLaBD?.imagen,
                onBack = { navController.popBackStack() },
                onIrAlFormulario = {
                    navController.navigate(RutasNavegacion.formularioReserva(nombreCancha))
                }
            )
        }

        // --- RUTA 2.2: FORMULARIO DE RESERVA ---
        composable(RutasNavegacion.FORMULARIO_RESERVA) { backStackEntry ->
            val nombreCancha = backStackEntry.arguments?.getString("nombreCancha") ?: ""
            val idAreaReal = areasViewModel.areas.value.find { it.nombre == nombreCancha }?.id ?: 1L
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L

            ReservaScreen(
                nombreCancha = nombreCancha,
                idArea = idAreaReal,
                idUsuario = idUsuario,
                viewModel = reservaViewModel,
                onBack = { navController.popBackStack() },
                onNavegarAreas = {
                    navController.popBackStack(RutasNavegacion.AREAS, inclusive = false)
                }
            )
        }

        // --- RUTA 2.3: EDITAR RESERVA ✅ NUEVA ---
        composable(RutasNavegacion.EDITAR_RESERVA) { backStackEntry ->
            val idReserva = backStackEntry.arguments?.getString("idReserva")?.toLongOrNull() ?: 0L

            // ✅ collectAsState() en vez de .value dentro de un Composable
            val reservasUsuario by areasViewModel.reservasUsuario.collectAsState()
            val areas by areasViewModel.areas.collectAsState()
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L

            // Buscamos los datos actuales de la reserva en la lista que ya tenemos
            val reservaActual = reservasUsuario.find { it.id == idReserva }

            // Buscamos el nombre del área para mostrarlo en la pantalla
            val areaDeReserva = areas.find { it.id == reservaActual?.area?.id }

            EditarReservaScreen(
                nombreCancha = areaDeReserva?.nombre ?: "Cancha",
                fechaActual = reservaActual?.fecha ?: "",
                horaInicioActual = reservaActual?.horaInicio ?: "",
                horaFinActual = reservaActual?.horaFin ?: "",
                descripcionActual = reservaActual?.descripcion ?: "",
                onGuardarCambios = { nuevaFecha, nuevaHoraInicio, nuevaHoraFin, nuevaDescripcion ->
                    reservaViewModel.actualizarReserva(
                        idReserva = idReserva,
                        idArea = reservaActual?.area?.id ?: 0L,
                        idUsuario = idUsuario,
                        fecha = nuevaFecha,
                        horaInicio = nuevaHoraInicio,
                        horaFin = nuevaHoraFin,
                        descripcion = nuevaDescripcion,
                        onExito = {
                            navController.popBackStack(RutasNavegacion.AREAS, inclusive = false)
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA 3: HISTORIAL ---
        composable(RutasNavegacion.HISTORIAL) {
            HistorialScreen(onBack = { navController.popBackStack() })
        }

        // --- RUTA 4: PERFIL ---
        composable(RutasNavegacion.PERFIL) {
            val usuario = authViewModel.usuarioLogueado
            PerfilScreen(
                nombreUsuario = usuario?.nombre ?: "Cargando...",
                correoUsuario = usuario?.emailInstitucional ?: "usuario@utez.edu.mx",
                onEditarClick = { navController.navigate(RutasNavegacion.EDITAR_PERFIL) },
                onCerrarSesionClick = {
                    navController.navigate(RutasNavegacion.INICIO) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- RUTA 4.1: EDITAR PERFIL ---
        composable(RutasNavegacion.EDITAR_PERFIL) {
            val usuario = authViewModel.usuarioLogueado
            EditarPerfilScreen(
                usuarioActual = usuario,
                onBack = { navController.popBackStack() },
                onGuardar = { nuevoTelefono, nuevaCarrera ->
                    if (usuario != null) {
                        val usuarioEditado = usuario.copy(
                            telefono = nuevoTelefono,
                            carrera = nuevaCarrera
                        )
                        usuarioViewModel.actualizarDatosPerfil(usuarioEditado) { exito ->
                            if (exito) navController.popBackStack()
                        }
                    }
                }
            )
        }
    }
}