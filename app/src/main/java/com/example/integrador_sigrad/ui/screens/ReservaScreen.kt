package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.viewmodel.ReservaViewModel
import com.example.integrador_sigrad.model.ReservaRequest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaScreen(
    nombreCancha: String,
    idArea: Long,
    idUsuario: Long,
    viewModel: ReservaViewModel,
    onBack: () -> Unit,
    onNavegarAreas: () -> Unit // <-- Cambiamos el nombre del callback
) {
    var fecha by remember { mutableStateOf("") }
    var horaEntrada by remember { mutableStateOf("") }
    var horaSalida by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val estaCargando by viewModel.estaCargando.collectAsState()
    val reservaExitosa by viewModel.reservaExitosa.collectAsState()
    val error by viewModel.error.collectAsState()

    var mostrarCalendario by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val colorBotonOscuro = Color(0xFF344356)
    val colorVerdeExito = Color(0xFF5CB85C)

    // =========================================================
    // 🌟 ESTA ES LA CARD QUE APARECE ENCIMA (POPUP)
    // =========================================================
    if (reservaExitosa) {
        AlertDialog(
            onDismissRequest = { /* Lo dejamos vacío para obligar al usuario a picar OK */ },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "Éxito", tint = colorVerdeExito)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¡Reserva Exitosa!", fontWeight = FontWeight.Bold, color = colorVerdeExito)
                }
            },
            text = {
                Text("Tu reserva para $nombreCancha se ha guardado correctamente en el sistema.", fontSize = 16.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarEstado() // Limpiamos el viewModel
                        onNavegarAreas() // Navegamos a la vista de áreas
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorVerdeExito)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }
    // =========================================================

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
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CenterAlignedTopAppBar(
            title = { Text(text = nombreCancha, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Text(
                text = "Realizar una reserva",
                fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp)
            )

            // 1. Fecha
            Text("Fecha:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = fecha, onValueChange = {}, enabled = false, readOnly = true,
                placeholder = { Text("Selecciona una fecha", color = Color.Gray) },
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario", tint = colorBotonOscuro) },
                modifier = Modifier.fillMaxWidth().clickable { if (!estaCargando) mostrarCalendario = true },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(disabledTextColor = Color.Black, disabledBorderColor = Color.LightGray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Horas
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hora entrada:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = horaEntrada, onValueChange = { horaEntrada = it },
                        placeholder = { Text("ej. 10:00") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Hora salida:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    OutlinedTextField(
                        value = horaSalida, onValueChange = { horaSalida = it },
                        placeholder = { Text("ej. 12:00") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Descripción
            Text("Motivo / Descripción:", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            OutlinedTextField(
                value = descripcion, onValueChange = { descripcion = it },
                placeholder = { Text("Motivo de la reserva...") },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(8.dp)
            )

            if (error != null) {
                Text(text = error!!, color = Color.Red, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // BOTÓN RESERVAR (Siempre visible de fondo)
            Button(
                onClick = {
                    val request = ReservaRequest(
                        idArea = idArea, idUsuario = idUsuario, fecha = fecha,
                        horaInicio = horaEntrada, horaFin = horaSalida, descripcion = descripcion
                    )
                    viewModel.crearReserva(request)
                },
                enabled = !estaCargando && fecha.isNotEmpty() && horaEntrada.isNotEmpty() && horaSalida.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = colorBotonOscuro)
            ) {
                if (estaCargando) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        //Para el card de confirmacion de la reserva.
        val reservaExitosa by viewModel.reservaExitosa.collectAsState()

        if (reservaExitosa) {
            AlertDialog(
                onDismissRequest = { }, // Evita que se cierre si tocan afuera
                title = {
                    Text(text = "¡Reserva Exitosa!", fontWeight = FontWeight.Bold)
                },
                text = {
                    Text(text = "La zona ha sido reservada correctamente.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.limpiarEstado() // Limpiamos el viewmodel
                            onNavegarAreas()          // Navegamos de regreso a las canchas
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C)) // Verde
                    ) {
                        Text("OK", color = Color.White)
                    }
                }
            )
        }
    }
}