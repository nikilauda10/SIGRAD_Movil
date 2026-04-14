package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.Schedule

// Función auxiliar para validar horas
fun timeToMin(time: String): Int {
    if (time.isEmpty()) return 0
    val parts = time.split(":")
    if (parts.size != 2) return 0
    return try {
        parts[0].toInt() * 60 + parts[1].toInt()
    } catch (e: Exception) { 0 }
}

// Wrapper para el reloj
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogEditar(
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
fun EditarReservaScreen(
    nombreCancha: String,
    fechaActual: String,
    horaInicioActual: String,
    horaFinActual: String,
    descripcionActual: String,
    onGuardarCambios: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    // ✅ EL SECRETO: remember(variable) obliga a la UI a actualizarse en cuanto el ViewModel manda los datos.
    var fecha by remember(fechaActual) { mutableStateOf(fechaActual) }
    var horaEntrada by remember(horaInicioActual) { mutableStateOf(horaInicioActual) }
    var horaSalida by remember(horaFinActual) { mutableStateOf(horaFinActual) }
    var descripcion by remember(descripcionActual) { mutableStateOf(descripcionActual) }

    // Estados para los modales
    var mostrarCalendario by remember { mutableStateOf(false) }
    var mostrarRelojInicio by remember { mutableStateOf(false) }
    var mostrarRelojFin by remember { mutableStateOf(false) }

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
                val esPasado = utcTimeMillis < hoy.timeInMillis // 🔴 Bloquea los días pasados en el calendario

                return !esDomingo && !esPasado
            }
        }
    )

    val timePickerStateInicio = rememberTimePickerState()
    val timePickerStateFin = rememberTimePickerState()

    var mensajeErrorLocal by remember { mutableStateOf<String?>(null) }
    var formularioValido by remember { mutableStateOf(true) }

    val colorBotonOscuro = Color(0xFF344356)
    val colorVerdeExito = Color(0xFF5CB85C)

    // ✅ VALIDACIONES DE HORA Y FECHA AL EDITAR
    LaunchedEffect(horaEntrada, horaSalida, fecha) {
        if (horaEntrada.isNotEmpty() && horaSalida.isNotEmpty()) {
            val minInicio = timeToMin(horaEntrada)
            val minFin = timeToMin(horaSalida)

            // Calculamos la hora y fecha actual
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechaHoy = formatoFecha.format(Date())
            val calendarioAhora = Calendar.getInstance()
            val minActual = calendarioAhora.get(Calendar.HOUR_OF_DAY) * 60 + calendarioAhora.get(Calendar.MINUTE)

            when {
                minInicio >= minFin -> {
                    mensajeErrorLocal = "La hora de salida debe ser posterior a la de entrada."
                    formularioValido = false
                }
                // 🔴 REGLA AÑADIDA: Si es hoy y seleccionó una hora vieja
                fecha == fechaHoy && minInicio < minActual -> {
                    mensajeErrorLocal = "No puedes seleccionar una hora que ya pasó."
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

    // CALENDARIO DIALOG
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
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar", color = Color.Gray) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // RELOJ INICIO DIALOG
    if (mostrarRelojInicio) {
        TimePickerDialogEditar(
            title = "Nueva hora de inicio",
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
        TimePickerDialogEditar(
            title = "Nueva hora de fin",
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
        // TOP BAR
        CenterAlignedTopAppBar(
            title = { Text(text = "Editar Reserva", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)) {

            // Mostramos el nombre del área claramente
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Área Deportiva:", fontSize = 12.sp, color = Color.Gray)
                    Text(text = nombreCancha, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = colorBotonOscuro)
                }
            }

            // 1. FECHA
            Text("Fecha:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                enabled = false,
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = colorBotonOscuro) },
                modifier = Modifier.fillMaxWidth().clickable { mostrarCalendario = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Black, disabledBorderColor = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. HORARIOS
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Hora Inicio
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hora Entrada:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = horaEntrada,
                        onValueChange = {},
                        enabled = false,
                        readOnly = true,
                        isError = !formularioValido,
                        trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Reloj", tint = colorBotonOscuro) },
                        modifier = Modifier.fillMaxWidth().clickable { mostrarRelojInicio = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = if (!formularioValido) Color.Red else Color.Black,
                            disabledBorderColor = if (!formularioValido) Color.Red else Color.Gray
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
                        isError = !formularioValido,
                        trailingIcon = { Icon(Icons.Default.Schedule, contentDescription = "Reloj", tint = colorBotonOscuro) },
                        modifier = Modifier.fillMaxWidth().clickable { mostrarRelojFin = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = if (!formularioValido) Color.Red else Color.Black,
                            disabledBorderColor = if (!formularioValido) Color.Red else Color.Gray
                        )
                    )
                }
            }

            // 🔴 MOSTRAR EL MENSAJE DE ERROR DE LA HORA PASADA
            if (mensajeErrorLocal != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = mensajeErrorLocal!!, color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. DESCRIPCIÓN
            Text("Motivo / Descripción:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // BOTÓN ACTUALIZAR
            Button(
                onClick = { onGuardarCambios(fecha, horaEntrada, horaSalida, descripcion) },
                enabled = formularioValido,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorVerdeExito)
            ) {
                Text("Actualizar Reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}