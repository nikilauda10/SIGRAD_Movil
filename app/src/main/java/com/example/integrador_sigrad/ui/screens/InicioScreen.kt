package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.ui.theme.ColorBotonGris
import com.example.integrador_sigrad.ui.theme.ColorBotonOscuro

@Composable
// 👇 AQUÍ agregamos los dos parámetros para la navegación
fun InicioScreen(
    onIrAAreas: () -> Unit,
    onIrAHistorial: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Inicio", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(40.dp))

        // Contenido alineado a la izquierda
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Bienvenid@", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Última reserva: 01/02/2026", fontSize = 16.sp, color = Color.DarkGray)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Hacer Reserva
        Button(
            onClick = onIrAAreas, // 👇 AQUÍ conectamos la acción
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro)
        ) {
            Text(text = "Hacer una reserva", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Ver Historial
        Button(
            onClick = onIrAHistorial, // 👇 AQUÍ conectamos la otra acción
            modifier = Modifier.fillMaxWidth().height(55.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonGris)
        ) {
            Text(text = "Ver historial", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}