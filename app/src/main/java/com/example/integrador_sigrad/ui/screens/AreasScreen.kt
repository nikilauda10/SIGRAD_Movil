package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.integrador_sigrad.model.AreaDeportiva
import com.example.integrador_sigrad.viewmodel.AreaDeportivaViewModel

val ColorBotonVerde = Color(0xFF5CB85C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(
    viewModel: AreaDeportivaViewModel,
    onReservarClick: (AreaDeportiva) -> Unit,
    onBack: () -> Unit
) {
    val areas by viewModel.areas.collectAsState()
    val estaCargando by viewModel.estaCargando.collectAsState()
    val error by viewModel.error.collectAsState()

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
                            onReservarClick = { onReservarClick(area) }
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
    onReservarClick: () -> Unit
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

            // 🌐 URL optimizada para cargar la imagen en segundo plano
            val imageUrl = "http://192.168.1.88:8080/api/areas/${area.id}/imagen"

            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = "Foto de ${area.nombre}",
                modifier = Modifier.fillMaxWidth().height(150.dp),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.LightGray)
                    }
                },
                error = {
                    ImagenPorDefecto(nombreCancha = area.nombre)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = area.nombre, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Horario: ${area.horaApertura} - ${area.horaCierre}", fontSize = 12.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            // Si está bloqueada o en mantenimiento, el botón se apaga
            if (area.estado.equals("bloqueada", ignoreCase = true) || area.estado.equals("mantenimiento", ignoreCase = true)) {
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.fillMaxWidth().height(45.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(disabledContainerColor = Color.Gray)
                ) {
                    Text("Área Bloqueada", color = Color.White, fontWeight = FontWeight.Bold)
                }
            } else {
                // Si está disponible, botón verde de reservar
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