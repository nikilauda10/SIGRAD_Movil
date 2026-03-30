package com.example.integrador_sigrad.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.integrador_sigrad.model.AreaDeportiva
import com.example.integrador_sigrad.model.ReservaResponse
import com.example.integrador_sigrad.viewmodel.AreaDeportivaViewModel

val ColorBotonVerde = Color(0xFF5CB85C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(
    viewModel: AreaDeportivaViewModel,
    idUsuarioActual: Long,
    onReservarClick: (AreaDeportiva) -> Unit,
    onEditarClick: (ReservaResponse) -> Unit,
    onCancelarClick: (AreaDeportiva, ReservaResponse) -> Unit,
    onBack: () -> Unit
) {
    val areas by viewModel.areas.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val error by viewModel.error.collectAsState()

    // Dialog de confirmación de cancelar
    var reservaAConfirmar by remember { mutableStateOf<ReservaResponse?>(null) }
    var areaDeReservaAConfirmar by remember { mutableStateOf<AreaDeportiva?>(null) }

    // BottomSheet para elegir qué reserva editar
    var areaParaElegirEdicion by remember { mutableStateOf<AreaDeportiva?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // ✅ DIALOG DE CONFIRMACIÓN CANCELAR
    if (reservaAConfirmar != null && areaDeReservaAConfirmar != null) {
        AlertDialog(
            onDismissRequest = {
                reservaAConfirmar = null
                areaDeReservaAConfirmar = null
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Text(
                    text = "¿Cancelar reserva?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro? Se cancelará la reserva de " +
                            "${reservaAConfirmar?.horaInicio} a ${reservaAConfirmar?.horaFin} " +
                            "del ${reservaAConfirmar?.fecha}.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelarClick(areaDeReservaAConfirmar!!, reservaAConfirmar!!)
                        reservaAConfirmar = null
                        areaDeReservaAConfirmar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sí, cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        reservaAConfirmar = null
                        areaDeReservaAConfirmar = null
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("No, volver", color = Color.DarkGray)
                }
            }
        )
    }

    // ✅ BOTTOM SHEET PARA ELEGIR QUÉ RESERVA EDITAR
    areaParaElegirEdicion?.let { area ->
        ModalBottomSheet(
            onDismissRequest = { areaParaElegirEdicion = null },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "¿Cuál reserva deseas editar?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                area.reservasActivas.forEach { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        onClick = {
                            areaParaElegirEdicion = null
                            onEditarClick(reserva)
                        }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "📅 ${reserva.fecha ?: "Sin fecha"}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⏰ ${reserva.horaInicio ?: "--"} - ${reserva.horaFin ?: "--"}",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                            if (!reserva.descripcion.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "📝 ${reserva.descripcion}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                OutlinedButton(
                    onClick = { areaParaElegirEdicion = null },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar", color = Color.DarkGray)
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Áreas Disponibles",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            estaCargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp), color = ColorBotonVerde)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("Error al cargar áreas", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(error ?: "Error desconocido", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.limpiarError(); viewModel.obtenerAreas() },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonVerde)
                        ) { Text("Reintentar", color = Color.White) }
                    }
                }
            }
            areas.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                        Text("No hay áreas disponibles", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Intenta más tarde", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(areas) { area ->
                        AreaCard(
                            area = area,
                            idUsuarioActual = idUsuarioActual,
                            onReservarClick = { onReservarClick(area) },
                            onEditarClick = {
                                // ✅ Si tiene 1 reserva va directo, si tiene más abre BottomSheet
                                when (area.reservasActivas.size) {
                                    1 -> onEditarClick(area.reservasActivas.first())
                                    else -> areaParaElegirEdicion = area
                                }
                            },
                            onCancelarClick = { reserva ->
                                reservaAConfirmar = reserva
                                areaDeReservaAConfirmar = area
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImagenPorDefecto(nombreCancha: String) {
    AsyncImage(
        model = "https://images.unsplash.com/photo-1518605368461-1ee12523ce70?q=80&w=1000&auto=format&fit=crop",
        contentDescription = "Foto por defecto de $nombreCancha",
        modifier = Modifier.fillMaxWidth().height(150.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AreaCard(
    area: AreaDeportiva,
    idUsuarioActual: Long,
    onReservarClick: () -> Unit,
    onEditarClick: () -> Unit,
    onCancelarClick: (ReservaResponse) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // IMAGEN
            var bitmap: android.graphics.Bitmap? = null
            if (!area.imagen.isNullOrEmpty() && area.imagen.length > 100) {
                try {
                    val base64String = area.imagen.substringAfter(",")
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                } catch (e: Exception) {
                    bitmap = null
                }
            }

            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto de ${area.nombre}",
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                ImagenPorDefecto(nombreCancha = area.nombre)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = area.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Horario: ${area.horaApertura} - ${area.horaCierre}", fontSize = 12.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            when {
                // CASO A: BLOQUEADA
                area.estado?.lowercase() == "bloqueada" -> {
                    Button(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray)
                    ) {
                        Text("Área Bloqueada", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // CASO B: TIENES RESERVAS → muestra todas con sus botones
                area.idUsuarioReserva == idUsuarioActual -> {
                    // Botones de acción globales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = onEditarClick,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text("Editar reserva", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onReservarClick,
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonVerde)
                        ) {
                            Text("+ Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ✅ Lista de todas las reservas activas con botón cancelar individual
                    Text(
                        text = "Tus reservas:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    area.reservasActivas.forEach { reserva ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "📅 ${reserva.fecha ?: "Sin fecha"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "⏰ ${reserva.horaInicio ?: "--"} - ${reserva.horaFin ?: "--"}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                    if (!reserva.descripcion.isNullOrBlank()) {
                                        Text(
                                            text = "📝 ${reserva.descripcion}",
                                            fontSize = 11.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Text(
                                        text = reserva.estado,
                                        fontSize = 11.sp,
                                        color = ColorBotonVerde,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Botón cancelar individual por reserva
                                IconButton(
                                    onClick = { onCancelarClick(reserva) }
                                ) {
                                    Text("✕", color = Color(0xFFF44336), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }

                // CASO C: DISPONIBLE
                else -> {
                    Button(
                        onClick = onReservarClick,
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ColorBotonVerde)
                    ) {
                        Text("Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}