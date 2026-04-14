package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.model.ReservaRequest
import com.example.integrador_sigrad.model.ReservaResponse
import com.example.integrador_sigrad.viewmodel.ReservaViewModel
import java.text.SimpleDateFormat
import java.util.*

// Función auxiliar para convertir "HH:mm" a minutos totales (facilita calcular choques)
fun timeToMinutes(time: String): Int {
    if (time.isEmpty()) return 0
    val parts = time.split(":")
    if (parts.size != 2) return 0
    return try {
        parts[0].toInt() * 60 + parts[1].toInt()
    } catch (e: Exception) { 0 }
}

// Verifica si las horas seleccionadas chocan con reservas existentes
fun hayChoqueDeHorarios(inicioNuevo: String, finNuevo: String, reservas: List<ReservaResponse>): Boolean {
    if (inicioNuevo.isEmpty() || finNuevo.isEmpty()) return false
    val minInicio = timeToMinutes(inicioNuevo)
    val minFin = timeToMinutes(finNuevo)

    return reservas.any { reserva ->
        val minExistenteInicio = timeToMinutes(reserva.horaInicio ?: "00:00")
        val minExistenteFin = timeToMinutes(reserva.horaFin ?: "00:00")
        // Fórmula matemática de traslape: (InicioA < FinB) y (FinA > InicioB)
        minInicio < minExistenteFin && minFin > minExistenteInicio
    }
}

// Componente Wrapper para el TimePicker de Material 3
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Selecciona la hora",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(size = 12.dp)),
        onDismissRequest = onDismissRequest,
        title = { Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = { content() },
        confirmButton = confirmButton,
        dismissButton = dismissButton
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    nombreCancha: String,
    idArea: Long,
    idUsuario: Long,
    horaApertura: String,
    horaCierre: String,
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
    val errorServidor by viewModel.error.collectAsState()
    val horasOcupadas by viewModel.horasOcupadas.collectAsState()

    // Estados para los modales
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarRelojInicio by remember { mutableStateOf(false) }
    var mostrarRelojFin by remember { mutableStateOf(false) }

    // Calendario bloqueando domingos
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = utcTimeMillis

                // Calculamos "Hoy"
                val hoy = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                hoy.set(Calendar.HOUR_OF_DAY, 0)
                hoy.set(Calendar.MINUTE, 0)
                hoy.set(Calendar.SECOND, 0)
                hoy.set(Calendar.MILLISECOND, 0)

                val esDomingo = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
                val esPasado = utcTimeMillis < hoy.timeInMillis // 🔴 Bloquea el pasado

                return !esDomingo && !esPasado
            }
        }
    )
    val timePickerStateInicio = rememberTimePickerState()
    val timePickerStateFin = rememberTimePickerState()

    // Lógica de validación en tiempo real del frontend
    var mensajeErrorLocal by remember { mutableStateOf<String?>(null) }
    var formularioValido by remember { mutableStateOf(false) }

    // Cada que el usuario cambia una hora, verificamos todo
    // Asegúrate de agregar 'fecha' en la lista del LaunchedEffect:
    LaunchedEffect(horaEntrada, horaSalida, horasOcupadas, fecha) {
        if (horaEntrada.isNotEmpty() && horaSalida.isNotEmpty()) {
            val minInicio = timeToMinutes(horaEntrada) // Usa timeToMin en EditarReservaScreen
            val minFin = timeToMinutes(horaSalida)     // Usa timeToMin en EditarReservaScreen

            // Calculamos la hora y fecha actual
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaHoy = formatoFecha.format(Date())
            val calendarioAhora = Calendar.getInstance()
            val minActual = calendarioAhora.get(Calendar.HOUR_OF_DAY) * 60 + calendarioAhora.get(Calendar.MINUTE)

            // Variables para los horarios de apertura (solo en ReservaScreen, en Editar elimínalas si no las usas)
            val minApertura = timeToMinutes(horaApertura)
            val minCierre = timeToMinutes(horaCierre)

            when {
                minInicio >= minFin -> {
                    mensajeErrorLocal = "La hora de salida debe ser mayor a la de entrada."
                    formularioValido = false
                }
                // 🔴 NUEVA REGLA: Si es hoy y seleccionó una hora vieja
                fecha == fechaHoy && minInicio < minActual -> {
                    mensajeErrorLocal = "No puedes seleccionar una hora que ya pasó."
                    formularioValido = false
                }
                minInicio < minApertura || minFin > minCierre -> {
                    mensajeErrorLocal = "Horario fuera de servicio."
                    formularioValido = false
                }
                hayChoqueDeHorarios(horaEntrada, horaSalida, horasOcupadas) -> {
                    mensajeErrorLocal = "¡Este horario ya está reservado por alguien más!"
                    formularioValido = false
                }
                else -> {
                    mensajeErrorLocal = null
                    formularioValido = true
                }
            }
        } else {
            mensajeErrorLocal = null
            formularioValido = false
        }
    }

    // Limpiamos el JSON crudo del servidor para que se vea estético
    val errorServidorLimpio = errorServidor?.let { err ->
        if (err.contains("\"mensaje\":\"")) {
            err.substringAfter("\"mensaje\":\"").substringBefore("\"")
        } else err
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
            text = { Text("Tu reserva para $nombreCancha se ha guardado correctamente.", fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarEstado()
                        viewModel.limpiarHoras()
                        onNavegarAreas()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorVerdeExito)
                ) { Text("OK", color = Color.White) }
            },
            containerColor = Color.White
        )
    }

    // CALENDARIO DIALOG
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    mostrarCalendario = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formateador = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        fecha = formateador.format(Date(millis + 86400000)) // Ajuste de zona horaria
                    }
                }) { Text("Aceptar", color = colorBotonOscuro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // RELOJ INICIO DIALOG
    if (mostrarRelojInicio) {
        TimePickerDialog(
            title = "Hora de inicio",
            onDismissRequest = { mostrarRelojInicio = false },
            confirmButton = {
                TextButton(onClick = {
                    mostrarRelojInicio = false
                    horaEntrada = String.format(Locale.getDefault(), "%02d:%02d", timePickerStateInicio.hour, timePickerStateInicio.minute)
                }) { Text("OK", color = colorBotonOscuro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarRelojInicio = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) { TimePicker(state = timePickerStateInicio) }
    }

    // RELOJ FIN DIALOG
    if (mostrarRelojFin) {
        TimePickerDialog(
            title = "Hora de fin",
            onDismissRequest = { mostrarRelojFin = false },
            confirmButton = {
                TextButton(onClick = {
                    mostrarRelojFin = false
                    horaSalida = String.format(Locale.getDefault(), "%02d:%02d", timePickerStateFin.hour, timePickerStateFin.minute)
                }) { Text("OK", color = colorBotonOscuro) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarRelojFin = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) { TimePicker(state = timePickerStateFin) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CenterAlignedTopAppBar(
            title = { Text(text = nombreCancha, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {
            Text(text = "Realizar una reserva", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))

            // 1. CAMPO FECHA
            Text("Fecha:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                placeholder = { Text("Selecciona una fecha", color = Color.Gray) },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = colorBotonOscuro) },
                modifier = Modifier.fillMaxWidth().clickable { if (!estaCargando) mostrarCalendario = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Black, disabledBorderColor = Color.Gray)
            )

            // 2. CAMPOS DE RELOJ (Solo se muestran si ya escogió fecha)
            if (fecha.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                val tieneErrorDeHorario = mensajeErrorLocal != null // Bandera para poner los inputs en rojo

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Hora Inicio
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora Entrada:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = horaEntrada,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            isError = tieneErrorDeHorario, // 🔴 Se pone rojo si hay error
                            placeholder = { Text("--:--", color = Color.Gray) },
                            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Reloj", tint = colorBotonOscuro) },
                            modifier = Modifier.fillMaxWidth().clickable { mostrarRelojInicio = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = if (tieneErrorDeHorario) Color.Red else Color.Black,
                                disabledBorderColor = if (tieneErrorDeHorario) Color.Red else Color.Gray
                            )
                        )
                    }
                    // Hora Fin
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Hora Salida:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                        OutlinedTextField(
                            value = horaSalida,
                            onValueChange = {},
                            enabled = false,
                            readOnly = true,
                            isError = tieneErrorDeHorario, // 🔴 Se pone rojo si hay error
                            placeholder = { Text("--:--", color = Color.Gray) },
                            trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Reloj", tint = colorBotonOscuro) },
                            modifier = Modifier.fillMaxWidth().clickable { mostrarRelojFin = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = if (tieneErrorDeHorario) Color.Red else Color.Black,
                                disabledBorderColor = if (tieneErrorDeHorario) Color.Red else Color.Gray
                            )
                        )
                    }
                }

                // MOSTRAR MENSAJE DE ERROR LOCAL (Choque de horarios o fuera de rango)
                if (mensajeErrorLocal != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = mensajeErrorLocal!!,
                        color = Color.Red,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. DESCRIPCIÓN (AHORA ES OPCIONAL)
            Text("Motivo / Descripción (Opcional):", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = { Text("Motivo de la reserva...") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )

            // Error del Servidor Limpiado
            if (errorServidorLimpio != null) {
                Text(text = errorServidorLimpio, color = Color.Red, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN RESERVAR
            Button(
                onClick = {
                    val request = ReservaRequest(idArea = idArea, idUsuario = idUsuario, fecha = fecha, horaInicio = horaEntrada, horaFin = horaSalida, descripcion = descripcion)
                    viewModel.crearReserva(request)
                },
                // ✅ EL BOTÓN SOLO SE ACTIVA SI LAS HORAS SON VÁLIDAS Y NO CHOCAN (La descripción ya no importa)
                enabled = formularioValido && !estaCargando,
                modifier = Modifier.fillMaxWidth().height(50.dp),
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