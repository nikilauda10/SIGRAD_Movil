package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.integrador_sigrad.model.Reserva
import com.example.integrador_sigrad.viewmodel.HistorialViewModel

@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel,
    idUsuario: Long,
    onBack: () -> Unit = {}
) {
    val reservas by viewModel.reservas.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // EFECTO DE AUTO-REFRESCO: Se dispara cada vez que la pantalla vuelve a primer plano
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.obtenerHistorial(idUsuario)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Historial", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FiltroChip(texto = "Fecha")
            FiltroChip(texto = "Estado")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Tus Reservas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        if (estaCargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF5CB85C))
            }
        } else if (reservas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No hay registros disponibles", color = Color.Gray)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reservas) { reserva ->
                    RegistroItemReal(reserva)
                }
            }
        }
    }
}

@Composable
fun RegistroItemReal(reserva: Reserva) {
    val colorEstado = when (reserva.estado.uppercase()) {
        "CONFIRMADA" -> Color(0xFF5CB85C) // Verde
        "CANCELADA" -> Color(0xFFF44336)  // Rojo
        "ACTUALIZADA" -> Color(0xFFFFA000) // Naranja
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = reserva.area.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(text = "Horario: ${reserva.horaInicio} - ${reserva.horaFin}", fontSize = 13.sp, color = Color.DarkGray)
                Text(text = "Fecha: ${reserva.fecha}", fontSize = 13.sp, color = Color.Gray)
            }

            Surface(
                color = colorEstado.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = reserva.estado,
                    color = colorEstado,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun FiltroChip(texto: String) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = texto, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
    }
}