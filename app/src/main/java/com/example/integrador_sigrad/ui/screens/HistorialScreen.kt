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
import androidx.compose.ui.text.style.TextAlign // ✅ Importación necesaria para centrar
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

    // ✅ Estados para los filtros (Ahora usando los nombres visuales)
    var filtroEstado by remember { mutableStateOf("Todos") }
    var filtroFecha by remember { mutableStateOf("Más reciente") }
    var expandirEstado by remember { mutableStateOf(false) }
    var expandirFecha by remember { mutableStateOf(false) }

    // Opciones del menú desplegable usando nombres amigables
    val opcionesEstado = listOf("Todos", "ACTIVA", "FINALIZADA", "CANCELADA")
    val opcionesFecha = listOf("Más reciente", "Más antigua")

    // ✅ Aplicamos los filtros sobre la lista completa
    val reservasFiltradas = remember(reservas, filtroEstado, filtroFecha) {
        var lista = reservas

        // Filtro por estado (Hacemos la traducción a la inversa para filtrar correctamente la base de datos)
        if (filtroEstado != "Todos") {
            val estadoDB = when (filtroEstado) {
                "ACTIVA" -> "CONFIRMADA"
                "FINALIZADA" -> "COMPLETADA"
                "CANCELADA" -> "CANCELADA"
                else -> filtroEstado
            }
            lista = lista.filter { it.estado.uppercase() == estadoDB }
        }

        // Filtro por fecha
        lista = when (filtroFecha) {
            "Más reciente" -> lista.sortedByDescending { it.fecha }
            "Más antigua"  -> lista.sortedBy { it.fecha }
            else -> lista
        }

        lista
    }

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Historial", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // ✅ DROPDOWNS DE FILTRO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown Fecha
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandirFecha = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(filtroFecha, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                DropdownMenu(
                    expanded = expandirFecha,
                    onDismissRequest = { expandirFecha = false }
                ) {
                    opcionesFecha.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                filtroFecha = opcion
                                expandirFecha = false
                            }
                        )
                    }
                }
            }

            // Dropdown Estado
            Box(modifier = Modifier.weight(1f)) {
                OutlinedButton(
                    onClick = { expandirEstado = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(filtroEstado, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                DropdownMenu(
                    expanded = expandirEstado,
                    onDismissRequest = { expandirEstado = false }
                ) {
                    opcionesEstado.forEach { opcion ->
                        DropdownMenuItem(
                            text = {
                                val color = when (opcion) {
                                    "ACTIVA" -> Color(0xFF4CAF50)      // Verde
                                    "FINALIZADA" -> Color(0xFF9E9E9E)  // Gris
                                    "CANCELADA" -> Color(0xFFF44336)   // Rojo
                                    else -> Color.Black
                                }
                                Text(opcion, color = color, fontWeight = FontWeight.SemiBold)
                            },
                            onClick = {
                                filtroEstado = opcion
                                expandirEstado = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Contador de resultados
        Text(
            text = "Tus Reservas (${reservasFiltradas.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            estaCargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF5CB85C))
                }
            }
            reservasFiltradas.isEmpty() && reservas.isNotEmpty() -> {
                // Hay reservas pero el filtro no muestra ninguna
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No hay reservas con estado \"$filtroEstado\"",
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { filtroEstado = "Todos" }) {
                            Text("Ver todas", color = Color(0xFF5CB85C))
                        }
                    }
                }
            }
            reservas.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No hay reservaciones realizadas", color = Color.Gray)
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp), // Espacio para la navegación inferior
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(reservasFiltradas) { reserva ->
                        RegistroItemReal(reserva)
                    }
                }
            }
        }
    }
}

@Composable
fun RegistroItemReal(reserva: Reserva) {
    // ✅ TRADUCCIÓN VISUAL: Asignamos nombre y colores según lo que manda la base de datos
    val (textoEstado, colorFondo, colorTexto) = when (reserva.estado.uppercase()) {
        "CONFIRMADA" -> Triple("ACTIVA", Color(0xFFE8F5E9), Color(0xFF4CAF50))
        "COMPLETADA" -> Triple("FINALIZADA", Color(0xFFEEEEEE), Color(0xFF9E9E9E))
        "CANCELADA"  -> Triple("CANCELADA", Color(0xFFFFEBEE), Color(0xFFF44336))
        else         -> Triple(reserva.estado.uppercase(), Color(0xFFFFF3E0), Color(0xFFFF9800)) // Naranja por defecto
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reserva.area.nombre,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⏰ ${reserva.horaInicio} - ${reserva.horaFin}",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "📅 ${reserva.fecha}",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                if (!reserva.descripcion.isNullOrBlank()) {
                    Text(
                        text = "📝 ${reserva.descripcion}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // ✅ ETIQUETA VISUAL ALINEADA (Mismo tamaño para todas)
            Surface(
                color = colorFondo,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.width(85.dp) // <-- Este es el truco para que midan igual
            ) {
                Text(
                    text = textoEstado,
                    color = colorTexto,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center, // <-- Esto asegura que quede bien centrado
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}