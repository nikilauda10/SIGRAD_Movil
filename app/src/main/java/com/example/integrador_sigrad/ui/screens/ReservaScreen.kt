package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.model.ReservaRequest
import com.example.integrador_sigrad.viewmodel.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.*

// Genera bloques de 15 minutos entre horaInicio y horaFin
// Ejemplo: "09:00" a "12:00" → ["09:00", "09:15", "09:30"...]
fun generarBloques(horaApertura: String, horaCierre: String): List<String> {
    val bloques = mutableListOf<String>()
    try {
        val partsInicio = horaApertura.split(":")
        val partsFin = horaCierre.split(":")
        var horaActual = partsInicio[0].toInt() * 60 + partsInicio[1].toInt()
        val horaFinal = partsFin[0].toInt() * 60 + partsFin[1].toInt()

        while (horaActual < horaFinal) {
            val h = horaActual / 60
            val m = horaActual % 60
            bloques.add(String.format("%02d:%02d", h, m))
            horaActual += 15
        }
    } catch (e: Exception) { }
    return bloques
}

// Verifica si un bloque de 15 min está dentro de alguna reserva ocupada
fun esBloqueOcupado(bloque: String, horasOcupadas: List<com.example.integrador_sigrad.model.ReservaResponse>): Boolean {
    val partes = bloque.split(":")
    val minBloque = partes[0].toInt() * 60 + partes[1].toInt()

    return horasOcupadas.any { reserva ->
        val inicio = reserva.horaInicio ?: return@any false
        val fin = reserva.horaFin ?: return@any false
        val partesInicio = inicio.split(":")
        val partesFin = fin.split(":")
        val minInicio = partesInicio[0].toInt() * 60 + partesInicio[1].toInt()
        val minFin = partesFin[0].toInt() * 60 + partesFin[1].toInt()
        minBloque >= minInicio && minBloque < minFin
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    nombreCancha: String,
    idArea: Long,
    idUsuario: Long,
    horaApertura: String,  // ✅ Nuevo parámetro
    horaCierre: String,    // ✅ Nuevo parámetro
    viewModel: ReservaViewModel,
    onBack: () -> Unit,
    onNavegarAreas: () -> Unit
) {
    var fecha by remember { mutableStateOf("") }
    var horaEntrada by remember { mutableStateOf("") }
    var horaSalida by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val estaCargando by viewModel.estaCargando.collectAsState()
    val reservaExitosa by viewModel.reservaExitosa.collectAsState()
    val error by viewModel.error.collectAsState()
    val horasOcupadas by viewModel.horasOcupadas.collectAsState()
    val cargandoHoras by viewModel.cargandoHoras.collectAsState()

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // ✅ Generamos los bloques de 15 min según el horario del área
    val bloques = remember(horaApertura, horaCierre) {
        generarBloques(horaApertura, horaCierre)
    }

    // Cuando cambia la fecha recargamos las horas ocupadas
    LaunchedEffect(fecha) {
        if (fecha.isNotEmpty()) {
            horaEntrada = ""
            horaSalida = ""
            viewModel.cargarHorasOcupadas(idArea, fecha)
        }
    }

    val colorBotonOscuro = Color(0xFF344356)
    val colorVerdeExito = Color(0xFF5CB85C)
    val colorVerde = Color(0xFF4CAF50)
    val colorRojo = Color(0xFFF44336)
    val colorSeleccionado = Color(0xFF2196F3)

    // DIALOG ÉXITO
    if (reservaExitosa) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Éxito", tint = colorVerdeExito)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¡Reserva Exitosa!", fontWeight = FontWeight.Bold, color = colorVerdeExito)
                }
            },
            text = {
                Text("Tu reserva para $nombreCancha se ha guardado correctamente.", fontSize = 16.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarEstado()
                        viewModel.limpiarHoras()
                        onNavegarAreas()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorVerdeExito)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    // CALENDARIO
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    mostrarCalendario = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formateador = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        fecha = formateador.format(Date(millis + 86400000))
                    }
                }) { Text("Aceptar", color = colorBotonOscuro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CenterAlignedTopAppBar(
            title = { Text(text = nombreCancha, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Realizar una reserva",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // 1. FECHA
            Text("Fecha:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                placeholder = { Text("Selecciona una fecha", color = Color.Gray) },
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = colorBotonOscuro)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { if (!estaCargando) mostrarCalendario = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.Black,
                    disabledBorderColor = Color.LightGray
                )
            )

            // 2. SELECTOR DE HORAS (solo aparece cuando hay fecha)
            if (fecha.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                // Leyenda de colores
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colorVerde, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Disponible", fontSize = 11.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colorRojo, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ocupado", fontSize = 11.sp, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colorSeleccionado, RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Seleccionado", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (cargandoHoras) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colorBotonOscuro, modifier = Modifier.size(32.dp))
                    }
                } else {
                    // ✅ GRID DE BLOQUES DE 15 MINUTOS
                    Text(
                        text = "Hora de entrada:",
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bloques) { bloque ->
                            val ocupado = esBloqueOcupado(bloque, horasOcupadas)
                            val esEntrada = bloque == horaEntrada
                            val esSalida = bloque == horaSalida

                            val colorFondo = when {
                                esEntrada || esSalida -> colorSeleccionado
                                ocupado -> colorRojo
                                else -> colorVerde
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(colorFondo)
                                    .then(
                                        if (!ocupado) Modifier.clickable {
                                            // Lógica de selección entrada → salida
                                            when {
                                                horaEntrada.isEmpty() -> horaEntrada = bloque
                                                horaSalida.isEmpty() && bloque > horaEntrada -> horaSalida = bloque
                                                else -> {
                                                    // Reinicia la selección
                                                    horaEntrada = bloque
                                                    horaSalida = ""
                                                }
                                            }
                                        } else Modifier
                                    )
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = bloque,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // Muestra la selección actual
                    if (horaEntrada.isNotEmpty() || horaSalida.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Entrada", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = horaEntrada.ifEmpty { "--:--" },
                                        fontWeight = FontWeight.Bold,
                                        color = colorSeleccionado,
                                        fontSize = 16.sp
                                    )
                                }
                                Text("→", fontSize = 20.sp, color = Color.Gray)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Salida", fontSize = 11.sp, color = Color.Gray)
                                    Text(
                                        text = horaSalida.ifEmpty { "--:--" },
                                        fontWeight = FontWeight.Bold,
                                        color = colorSeleccionado,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. DESCRIPCIÓN
            Text("Motivo / Descripción:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Motivo de la reserva...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )

            if (error != null) {
                Text(
                    text = error!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN RESERVAR
            Button(
                onClick = {
                    val request = ReservaRequest(
                        idArea = idArea,
                        idUsuario = idUsuario,
                        fecha = fecha,
                        horaInicio = horaEntrada,
                        horaFin = horaSalida,
                        descripcion = descripcion
                    )
                    viewModel.crearReserva(request)
                },
                enabled = !estaCargando && fecha.isNotEmpty() && horaEntrada.isNotEmpty() && horaSalida.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonOscuro)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}