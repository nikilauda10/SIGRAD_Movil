package com.example.integrador_sigrad.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreaDetalleScreen(
    nombreCancha: String,
    descripcionCancha: String,
    imagenUrl: String?,
    onBack: () -> Unit,
    onIrAlFormulario: () -> Unit
) {
    val colorBotonVerde = Color(0xFF5CB85C)
    val degradadoFondo = Brush.verticalGradient(
        colors = listOf(Color(0xFF3B4C5A), Color(0xFFF3F4F6))
    )

    // 👇 AQUÍ ESTÁ LA MAGIA: Procesamos la foto de texto a imagen SOLO UNA VEZ
    val bitmapDecodificado = remember(imagenUrl) {
        if (!imagenUrl.isNullOrBlank() && !imagenUrl.startsWith("http")) {
            try {
                val cleanBase64 = imagenUrl
                    .replace("data:image/jpeg;base64,", "")
                    .replace("data:image/png;base64,", "")
                val imageBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null // Si falla, regresa nulo
            }
        } else {
            null
        }
    }

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

                        // 📸 MOSTRAR LA FOTO (Ya sin cálculos pesados aquí)
                        if (!imagenUrl.isNullOrBlank()) {
                            if (imagenUrl.startsWith("http")) {
                                // Es un link normal de internet
                                AsyncImage(
                                    model = imagenUrl,
                                    contentDescription = "Foto de $nombreCancha",
                                    modifier = Modifier.fillMaxWidth().height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (bitmapDecodificado != null) {
                                // Es una imagen Base64 que logramos decodificar arriba
                                Image(
                                    bitmap = bitmapDecodificado.asImageBitmap(),
                                    contentDescription = "Foto de $nombreCancha",
                                    modifier = Modifier.fillMaxWidth().height(180.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                CajaGrisSinFoto()
                            }
                        } else {
                            CajaGrisSinFoto()
                        }

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