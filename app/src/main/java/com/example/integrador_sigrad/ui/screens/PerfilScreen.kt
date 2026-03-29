package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PerfilScreen(
    nombreUsuario: String,   // 👈 Agregado para recibir del Back
    correoUsuario: String,   // 👈 Agregado para recibir del Back
    onEditarClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
        )

        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFEFECE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                modifier = Modifier.size(80.dp),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 👇 AHORA USA LOS DATOS REALES QUE VIENEN POR PARÁMETRO
        Text(text = nombreUsuario, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = correoUsuario, fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(48.dp))

        // Botón Editar Perfil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onEditarClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Editar Perfil", fontSize = 18.sp)
            Icon(Icons.Default.Edit, contentDescription = "Editar")
        }

        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)

        // Botón Cerrar Sesión
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCerrarSesionClick() }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Cerrar Sesión", fontSize = 18.sp, color = Color.Red)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar Sesión",
                tint = Color.Red
            )
        }
    }
}