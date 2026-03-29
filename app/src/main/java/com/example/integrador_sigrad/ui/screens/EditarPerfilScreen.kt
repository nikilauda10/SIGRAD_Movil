package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.integrador_sigrad.model.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    usuarioActual: Usuario?,
    onBack: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    // Estados para los únicos campos editables
    var telefono by remember { mutableStateOf(usuarioActual?.telefono ?: "") }
    var carrera by remember { mutableStateOf(usuarioActual?.carrera ?: "") }

    val colorBotonOscuro = Color(0xFF344356)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPOS BLOQUEADOS (Vienen del back y no se pueden cambiar)
            CampoPerfil(label = "Nombre", valor = usuarioActual?.nombre ?: "", editable = false) {}
            CampoPerfil(label = "Matrícula", valor = usuarioActual?.matricula ?: "", editable = false) {}
            CampoPerfil(label = "Correo Institucional", valor = usuarioActual?.emailInstitucional ?: "", editable = false) {}

            // CAMPOS EDITABLES
            CampoPerfil(label = "Teléfono", valor = telefono, editable = true) { telefono = it }
            CampoPerfil(label = "Carrera", valor = carrera, editable = true) { carrera = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onGuardar(telefono, carrera) },
                modifier = Modifier.fillMaxWidth().height(50.dp).padding(bottom = 24.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonOscuro)
            ) {
                Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CampoPerfil(label: String, valor: String, editable: Boolean, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(text = label, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            enabled = editable,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = Color.DarkGray,
                disabledContainerColor = Color(0xFFF3F4F6),
                disabledBorderColor = Color.Transparent
            )
        )
    }
}