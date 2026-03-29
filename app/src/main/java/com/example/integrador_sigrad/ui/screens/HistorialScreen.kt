package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistorialScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        // Título central
        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Historial", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        // Botones de Filtro (Fecha y Área)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FiltroChip(texto = "Fecha")
            FiltroChip(texto = "Área")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Subtítulo
        Text(text = "Registros", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de Historial (Usamos LazyColumn por si son muchos registros)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item { RegistroItem(cancha = "Cancha de fútbol", fecha = "10/11/2026") }
            item { RegistroItem(cancha = "Cancha de basquetbol", fecha = "11/11/2026") }
            item { RegistroItem(cancha = "Área de natación", fecha = "12/11/2026") }
            item { RegistroItem(cancha = "Cancha de fútbol", fecha = "13/11/2026") }
            item { RegistroItem(cancha = "Cancha volleyball", fecha = "14/11/2026") }
        }
    }
}

// Componente para los botoncitos grises de filtro
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
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Desplegar", modifier = Modifier.size(18.dp))
    }
}

// Componente para cada fila de la lista
@Composable
fun RegistroItem(cancha: String, fecha: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = cancha, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = fecha, fontSize = 14.sp, color = Color.Gray)
    }
}