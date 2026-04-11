package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.model.Reserva
import com.example.integrador_sigrad.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    idUsuario: Long,
    nombreUsuario: String,
    viewModel: InicioViewModel,
    onIrAAreas: () -> Unit,
    onIrAHistorial: () -> Unit,
    onEditarReserva: (Reserva) -> Unit,
    onCancelarReserva: (Reserva) -> Unit
) {
    val reservasActivas by viewModel.reservasActivas.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()

    // ✅ Sintaxis correcta para una variable de estado que puede ser nula
    var reservaACancelar by remember { mutableStateOf<Reserva?>(null) }

    // Al entrar a la pantalla, cargamos las reservas
    LaunchedEffect(idUsuario) {
        viewModel.cargarReservasDelUsuario(idUsuario)
    }

    // DIALOG DE CONFIRMACIÓN CANCELAR
    if (reservaACancelar != null) {
        AlertDialog(
            onDismissRequest = { reservaACancelar = null },
            containerColor = Color.White,
            title = { Text("¿Cancelar reserva?", fontWeight = FontWeight.Bold) },
            text = { Text("Esta acción liberará el área deportiva. ¿Estás seguro?") },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelarReserva(reservaACancelar!!)
                        reservaACancelar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) { Text("Sí, cancelar", color = Color.White) }
            },
            dismissButton = {
                OutlinedButton(onClick = { reservaACancelar = null }) { Text("Volver") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // ✅ Saludo
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (nombreUsuario.isNotEmpty()) "¡Hola, $nombreUsuario!" else "Bienvenid@",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Text(
                text = "¿Qué deporte practicaremos hoy?",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        when {
            estaCargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF344356))
                }
            }
            // 🔴 SI NO HAY RESERVAS ACTIVAS -> Muestra los botones de siempre
            reservasActivas.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No tienes reservas activas.",
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Button(
                        onClick = onIrAAreas,
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF344356))
                    ) {
                        Text("Hacer una reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onIrAHistorial,
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                    ) {
                        Text("Ver historial", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
            // 🟢 SI HAY RESERVAS ACTIVAS -> Oculta botones y muestra las Cards
            else -> {
                Text(
                    text = "Tus Reservas Activas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp) // Espacio para la barra de navegación abajo
                ) {
                    items(reservasActivas) { reserva ->
                        ReservaActivaCard(
                            reserva = reserva,
                            onEditarClick = { onEditarReserva(reserva) },
                            onCancelarClick = { reservaACancelar = reserva }
                        )
                    }
                }
            }
        }
    }
}

// ✅ COMPONENTE: Tarjeta individual para cada reserva
@Composable
fun ReservaActivaCard(
    reserva: Reserva,
    onEditarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Estado y Área
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = "Ubicación", tint = Color(0xFF2196F3), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = reserva.area.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                // ✅ Traducción visual del estado que viene de la base de datos
                val (textoEstado, colorFondo, colorTexto) = when (reserva.estado.uppercase()) {
                    "CONFIRMADA" -> Triple("ACTIVA", Color(0xFFE8F5E9), Color(0xFF4CAF50))       // Fondo verde, Letra verde
                    "COMPLETADA" -> Triple("FINALIZADA", Color(0xFFEEEEEE), Color(0xFF9E9E9E))   // Fondo gris, Letra gris
                    "CANCELADA"  -> Triple("CANCELADA", Color(0xFFFFEBEE), Color(0xFFF44336))    // Fondo rojo, Letra roja
                    else         -> Triple(reserva.estado.uppercase(), Color.LightGray, Color.DarkGray)
                }

                // Etiqueta visual con los colores dinámicos
                Box(
                    modifier = Modifier
                        .background(colorFondo, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = textoEstado, color = colorTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detalles de Fecha y Hora
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = "Fecha", tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = reserva.fecha, fontSize = 14.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Schedule, contentDescription = "Hora", tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${reserva.horaInicio} - ${reserva.horaFin}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción independientes por tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancelarClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF44336))
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onEditarClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Editar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}