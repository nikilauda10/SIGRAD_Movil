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
import com.example.integrador_sigrad.viewmodel.HistorialViewModel
import com.example.integrador_sigrad.viewmodel.UsuarioViewModel
import com.example.integrador_sigrad.viewmodel.ReservaViewModel
import com.example.integrador_sigrad.viewmodel.InicioViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = RutasNavegacion.INICIO,
    authViewModel: AuthViewModel,
    onCerrarSesion: () -> Unit
) {
    val areasViewModel: AreaDeportivaViewModel = viewModel()
    val usuarioViewModel: UsuarioViewModel = viewModel()
    val reservaViewModel: ReservaViewModel = viewModel()
    val inicioViewModel: InicioViewModel = viewModel() // ✅ INICIALIZADO EL NUEVO VIEWMODEL

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- RUTA 1: INICIO ---
        composable(RutasNavegacion.INICIO) {
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 0L

            // ✅ Usamos la nueva InicioScreen que pide el viewModel y las funciones de acción
            InicioScreen(
                idUsuario = idUsuario,
                nombreUsuario = authViewModel.usuarioLogueado?.nombre ?: "",
                viewModel = inicioViewModel,
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
                },
                onEditarReserva = { reserva ->
                    // Navegamos a la pantalla de edición pasándole el ID de la reserva
                    navController.navigate(RutasNavegacion.editarReserva(reserva.id))
                },
                onCancelarReserva = { reserva ->
                    // Cancelamos la reserva usando la lógica de áreasViewModel (como lo hacías antes)
                    areasViewModel.cancelarReservaEspecifica(reserva.id, idUsuario)
                    // Y recargamos la pantalla de inicio para que desaparezca la tarjeta
                    inicioViewModel.cargarReservasDelUsuario(idUsuario)
                }
            )
        }

        // --- RUTA 2: LISTADO DE ÁREAS ---
        composable(RutasNavegacion.AREAS) {
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 0L
            val backStackEntry = navController.currentBackStackEntryAsState()
            val triggerRecarga by areasViewModel.triggerRecarga.collectAsState()

            // Solo carga cuando idUsuario es válido
            LaunchedEffect(backStackEntry.value?.destination?.route, triggerRecarga, idUsuario) {
                if (backStackEntry.value?.destination?.route == RutasNavegacion.AREAS && idUsuario > 0L) {
                    areasViewModel.cargarTodo(idUsuario)
                }
            }

            // Llamamos a la pantalla limpia
            AreasScreen(
                viewModel = areasViewModel,
                onReservarClick = { area ->
                    navController.navigate(RutasNavegacion.formularioReserva(area.nombre))
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA 2.1: DETALLE DEL ÁREA ---
        composable(RutasNavegacion.DETALLE_AREA) { backStackEntry ->
            val nombreCancha = backStackEntry.arguments?.getString("nombreCancha") ?: "Área"
            val listaDeAreas by areasViewModel.areas.collectAsState()
            val canchaDeLaBD = listaDeAreas.find { it.nombre == nombreCancha }
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L

            println("🔍 ID USUARIO EN AREAS: $idUsuario")
            println("🔍 USUARIO LOGUEADO: ${authViewModel.usuarioLogueado}")
            val textoUbicacion = if (canchaDeLaBD != null && canchaDeLaBD.ubicacion.isNotBlank()) {
                "📍 Ubicación: ${canchaDeLaBD.ubicacion}"
            } else {
                "Ubicación no disponible."
            }

            AreaDetalleScreen(
                nombreCancha = nombreCancha,
                descripcionCancha = textoUbicacion,
                idArea = canchaDeLaBD?.id ?: 0L, // 👈 SOLUCIÓN: Pasamos el ID del área en vez del Base64
                onBack = { navController.popBackStack() },
                onIrAlFormulario = {
                    navController.navigate(RutasNavegacion.formularioReserva(nombreCancha))
                }
            )
        }

        // --- RUTA 2.2: FORMULARIO DE RESERVA ---
        composable(RutasNavegacion.FORMULARIO_RESERVA) { backStackEntry ->
            val nombreCancha = backStackEntry.arguments?.getString("nombreCancha") ?: ""
            val areaReal = areasViewModel.areas.collectAsState().value.find { it.nombre == nombreCancha }
            val idAreaReal = areaReal?.id ?: 1L
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L

            ReservaScreen(
                nombreCancha = nombreCancha,
                idArea = idAreaReal,
                idUsuario = idUsuario,
                horaApertura = areaReal?.horaApertura ?: "08:00", // ✅ Pasa el horario real
                horaCierre = areaReal?.horaCierre ?: "20:00",     // ✅ Pasa el horario real
                viewModel = reservaViewModel,
                onBack = { navController.popBackStack() },
                onNavegarAreas = {
                    // ✅ Al terminar la reserva, regresamos al INICIO para que el usuario vea su nueva tarjeta
                    navController.popBackStack(RutasNavegacion.INICIO, inclusive = false)
                }
            )
        }

        // --- RUTA 2.3: EDITAR RESERVA ✅ CORREGIDA ---
        composable(RutasNavegacion.EDITAR_RESERVA) { backStackEntry ->
            val idReserva = backStackEntry.arguments?.getString("idReserva")?.toLongOrNull() ?: 0L
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 1L

            // ✅ CORRECCIÓN CLAVE: Traemos las listas de ambos ViewModels
            val reservasActivas by inicioViewModel.reservasActivas.collectAsState()
            val reservasDesdeAreas by areasViewModel.reservasUsuario.collectAsState()

            // Buscamos la reserva en el InicioViewModel primero, y si no está, la buscamos en el AreasViewModel
            val reservaActual = reservasActivas.find { it.id == idReserva }
                ?: reservasDesdeAreas.find { it.id == idReserva }

            EditarReservaScreen(
                nombreCancha = reservaActual?.area?.nombre ?: "Cargando área...",
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
                            // ✅ Forzamos a recargar el Inicio para que el cambio se vea reflejado inmediatamente
                            inicioViewModel.cargarReservasDelUsuario(idUsuario)
                            navController.popBackStack(RutasNavegacion.INICIO, inclusive = false)
                        }
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // --- RUTA 3: HISTORIAL ---
        composable(RutasNavegacion.HISTORIAL) {
            // 1. Obtenemos el ID del usuario logueado (importante para filtrar sus reservas)
            // Ajusta "usuarioLogueado" según como lo tengas en tu AuthViewModel
            val idUsuario = authViewModel.usuarioLogueado?.id ?: 0L

            // 2. Obtenemos el ViewModel del historial
            val historialViewModel: HistorialViewModel = viewModel()

            // 3. Llamamos a la pantalla pasando los datos necesarios
            HistorialScreen(
                viewModel = historialViewModel,
                idUsuario = idUsuario,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- RUTA 4: PERFIL ---
        composable(RutasNavegacion.PERFIL) {
            val usuario = authViewModel.usuarioLogueado
            PerfilScreen(
                nombreUsuario = usuario?.nombre ?: "Cargando...",
                correoUsuario = usuario?.emailInstitucional ?: "usuario@utez.edu.mx",
                onEditarClick = { navController.navigate(RutasNavegacion.EDITAR_PERFIL) },
                onCerrarSesionClick = {
                    authViewModel.cerrarSesion() // ✅ Usa la función del ViewModel
                    onCerrarSesion()             // ✅ Usa el callback de MainActivity
                }
            )
        }

        // --- RUTA 4.1: EDITAR PERFIL ---
        composable(RutasNavegacion.EDITAR_PERFIL) {
            val usuario = authViewModel.usuarioLogueado

            EditarPerfilScreen(
                usuarioActual = usuario,
                listaCarreras = authViewModel.listaCarreras, // 👇 ¡SOLO AGREGA ESTA LÍNEA!
                viewModel = usuarioViewModel,
                onBack = { navController.popBackStack() },
                onGuardar = { nuevoTelefono, nuevaCarrera ->
                    if (usuario != null) {
                        val usuarioEditado = usuario.copy(
                            telefono = nuevoTelefono,
                            carrera = nuevaCarrera
                        )
                        usuarioViewModel.actualizarDatosPerfil(usuarioEditado) { exito ->
                            if (exito) {
                                authViewModel.actualizarUsuarioLocal(nuevoTelefono, nuevaCarrera)
                            }
                        }
                    }
                }
            )
        }
    }
}