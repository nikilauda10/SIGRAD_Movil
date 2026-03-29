package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarReservaScreen(
    nombreCancha: String,
    fechaActual: String,
    horaInicioActual: String,
    horaFinActual: String,
    descripcionActual: String,
    onGuardarCambios: (String, String, String, String) -> Unit, // Regresa los nuevos datos
    onBack: () -> Unit
) {
    // Variables de estado que inician con los datos que ya tenía la reserva
    var fecha by remember { mutableStateOf(fechaActual) }
    var horaEntrada by remember { mutableStateOf(horaInicioActual) }
    var horaSalida by remember { mutableStateOf(horaFinActual) }
    var descripcion by remember { mutableStateOf(descripcionActual) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(text = "Editar reserva: $nombreCancha", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Fecha
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha nueva") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Campos Horarios
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = horaEntrada,
                onValueChange = { horaEntrada = it },
                label = { Text("Hora entrada") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = horaSalida,
                onValueChange = { horaSalida = it },
                label = { Text("Hora salida") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Campo Descripción
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Motivo / Descripción") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.weight(1f)) // Empuja los botones hacia abajo

        // Botones de Acción
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Regresar")
            }
            Button(
                onClick = { onGuardarCambios(fecha, horaEntrada, horaSalida, descripcion) },
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C))
            ) {
                Text("Actualizar")
            }
        }
    }
}