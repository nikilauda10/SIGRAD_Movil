package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.model.Usuario
import com.example.integrador_sigrad.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    usuarioActual: Usuario?,
    onBack: () -> Unit,
    onGuardar: (String, String) -> Unit,
    viewModel: UsuarioViewModel  // ✅ Agregamos el ViewModel para mostrar loading/error
) {

    var telefono by remember(usuarioActual) { mutableStateOf(usuarioActual?.telefono ?: "") }
    var carrera by remember(usuarioActual) { mutableStateOf(usuarioActual?.carrera ?: "") }

    val colorBotonOscuro = Color(0xFF344356)
    val colorVerde = Color(0xFF5CB85C)

    // ✅ DIALOG DE ÉXITO
    if (viewModel.exitoActualizacion) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colorVerde)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("¡Datos actualizados!", fontWeight = FontWeight.Bold, color = colorVerde)
                }
            },
            text = {
                Text("Tu teléfono y carrera han sido actualizados correctamente.", fontSize = 15.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarEstados()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorVerde),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

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

            // ── CAMPOS BLOQUEADOS ──────────────────────────────
            CampoPerfil(
                label = "Nombre",
                valor = usuarioActual?.nombre ?: "",
                editable = false
            ) {}
            CampoPerfil(
                label = "Matrícula",
                valor = usuarioActual?.matricula ?: "",
                editable = false
            ) {}
            CampoPerfil(
                label = "Correo Institucional",
                valor = usuarioActual?.emailInstitucional ?: "",
                editable = false
            ) {}
            CampoPerfil(
                label = "Rol",
                valor = usuarioActual?.rol ?: "",
                editable = false
            ) {}

            // ── CAMPOS EDITABLES ───────────────────────────────
            CampoPerfil(
                label = "Teléfono",
                valor = telefono,
                editable = true
            ) { telefono = it }

            CampoPerfil(
                label = "Carrera",
                valor = carrera,
                editable = true
            ) { carrera = it }

            // ── ERROR ──────────────────────────────────────────
            if (viewModel.mensajeError != null) {
                Text(
                    text = viewModel.mensajeError!!,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── BOTÓN GUARDAR ──────────────────────────────────
            Button(
                onClick = { onGuardar(telefono, carrera) },
                enabled = !viewModel.estaActualizando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonOscuro)
            ) {
                if (viewModel.estaActualizando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CampoPerfil(
    label: String,
    valor: String,
    editable: Boolean,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            enabled = editable,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                // Campos editables — borde normal
                focusedBorderColor = Color(0xFF344356),
                unfocusedBorderColor = Color.LightGray,
                // Campos bloqueados — fondo gris sin borde
                disabledTextColor = Color.DarkGray,
                disabledContainerColor = Color(0xFFF3F4F6),
                disabledBorderColor = Color.Transparent
            )
        )
    }
}