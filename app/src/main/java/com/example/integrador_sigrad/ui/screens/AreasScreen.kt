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
import com.example.integrador_sigrad.viewmodel.AreaDeportivaViewModel

val ColorBotonVerde = Color(0xFF5CB85C)

@Composable
fun AreasScreen(
    viewModel: AreaDeportivaViewModel,
    idUsuarioActual: Long,
    onReservarClick: (AreaDeportiva) -> Unit,
    onEditarClick: (AreaDeportiva) -> Unit,
    onCancelarClick: (AreaDeportiva) -> Unit,
    onBack: () -> Unit
) {
    val areas by viewModel.areas.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val error by viewModel.error.collectAsState()

    // ✅ Estado que controla qué área está esperando confirmación de cancelar
    var areaAConfirmar by remember { mutableStateOf<AreaDeportiva?>(null) }

    // ✅ DIALOG DE CONFIRMACIÓN DE CANCELAR
    areaAConfirmar?.let { area ->
        AlertDialog(
            onDismissRequest = { areaAConfirmar = null },
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
                    text = "¿Estás seguro que deseas cancelar tu reserva en ${area.nombre}? Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelarClick(area) // ✅ Ejecuta la cancelación real
                        areaAConfirmar = null  // Cierra el dialog
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sí, cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { areaAConfirmar = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("No, volver", color = Color.DarkGray)
                }
            }
        )
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = ColorBotonVerde
                    )
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Error al cargar áreas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "Error desconocido",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.limpiarError()
                                viewModel.obtenerAreas()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonVerde)
                        ) {
                            Text("Reintentar", color = Color.White)
                        }
                    }
                }
            }

            areas.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "No hay áreas disponibles",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Intenta más tarde",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
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
                            onEditarClick = { onEditarClick(area) },
                            // ✅ En vez de cancelar directo, abre el dialog
                            onCancelarClick = { areaAConfirmar = area }
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
    onCancelarClick: () -> Unit
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

            Text(
                text = area.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Horario: ${area.horaApertura} - ${area.horaCierre}",
                fontSize = 12.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            // LÓGICA DE BOTONES
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
                        Text(text = "Área Bloqueada", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                // CASO B: RESERVADA POR TI
                area.idUsuarioReserva == idUsuarioActual -> {
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
                            Text("Editar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = onCancelarClick, // ✅ Esto abre el dialog, no cancela directo
                            modifier = Modifier.weight(1f).padding(start = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // CASO C: DISPONIBLE
                else -> {
                    Button(
                        onClick = onReservarClick,
                        modifier = Modifier.fillMaxWidth().height(45.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5CB85C))
                    ) {
                        Text(text = "Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
