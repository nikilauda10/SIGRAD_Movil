package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage // 👈 Usamos esto para el Lazy Loading pro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaDetalleScreen(
    nombreCancha: String,
    descripcionCancha: String,
    idArea: Long, // 👈 CAMBIADO: Ahora recibe el ID en lugar del texto de la imagen
    onBack: () -> Unit,
    onIrAlFormulario: () -> Unit
) {
    val colorBotonVerde = Color(0xFF5CB85C)
    val degradadoFondo = Brush.verticalGradient(
        colors = listOf(Color(0xFF3B4C5A), Color(0xFFF3F4F6))
    )

    // 🌐 URL optimizada para cargar la imagen en segundo plano
    // ⚠️ IMPORTANTE: Pon la misma IP que tienes en tu RetrofitClient
    val imageUrl = "http://192.168.1.88:8080/api/areas/${idArea}/imagen"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Áreas Disponibles", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxHeight(0.85f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(degradadoFondo),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f).wrapContentHeight(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {

                        // 📸 LA MAGIA DEL LAZY LOADING 📸
                        SubcomposeAsyncImage(
                            model = imageUrl,
                            contentDescription = "Foto de $nombreCancha",
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop,
                            loading = {
                                // Muestra un círculo de carga mientras la imagen se descarga
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color.Gray)
                                }
                            },
                            error = {
                                // Si el backend responde 404 (no hay foto), muestra la caja gris
                                CajaGrisSinFoto()
                            }
                        )

                        // 🏷️ Título
                        Text(
                            text = nombreCancha,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                        )

                        // 📝 Descripción Armada
                        Text(
                            text = descripcionCancha,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 🟩 Botón Reservar
                        Button(
                            onClick = onIrAlFormulario,
                            modifier = Modifier.fillMaxWidth(0.5f).height(45.dp),
                            shape = RoundedCornerShape(25.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorBotonVerde)
                        ) {
                            Text("Reservar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CajaGrisSinFoto() {
    Box(
        modifier = Modifier.fillMaxWidth().height(180.dp).background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Text("Foto no disponible", color = Color.DarkGray)
    }
}