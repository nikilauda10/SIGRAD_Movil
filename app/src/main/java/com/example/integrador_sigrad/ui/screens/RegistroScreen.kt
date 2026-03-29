package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integrador_sigrad.model.Usuario
import com.example.integrador_sigrad.ui.components.CustomTextField
import com.example.integrador_sigrad.ui.theme.ColorBotonOscuro
import com.example.integrador_sigrad.ui.theme.ColorFondoInput
import com.example.integrador_sigrad.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Alumno") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    val opcionesRol = listOf("Alumno", "Docente")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Atrás") }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

            // Tu formulario normal
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                CustomTextField(label = "Nombre:*", placeholder = "Ej. Juan Pérez", value = nombre, onValueChange = { nombre = it })
                CustomTextField(label = "Matrícula:*", placeholder = "Ej. 123456", value = matricula, onValueChange = { matricula = it })
                CustomTextField(label = "Correo Institucional:*", placeholder = "correo@escuela.edu.mx", value = correo, onValueChange = { correo = it })
                CustomTextField(label = "Teléfono:*", placeholder = "Ej. 5512345678", value = telefono, onValueChange = { telefono = it })
                CustomTextField(label = "Carrera:*", placeholder = "Ej. Sistemas", value = carrera, onValueChange = { carrera = it })

                CustomDropdownMenu(
                    label = "Rol:*",
                    opciones = opcionesRol,
                    seleccionActual = rol,
                    onSeleccionCambiada = { rol = it }
                )

                CustomPasswordField(value = password, onValueChange = { password = it })

                // Mensaje de error dinámico
                if (viewModel.errorMessage != null) {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Button(
                    onClick = {
                        val nuevoUsuario = Usuario(
                            nombre = nombre,
                            matricula = matricula,
                            emailInstitucional = correo,
                            carrera = carrera,
                            rol = rol,
                            contrasena = password,
                            telefono = telefono
                        )
                        // 👇 Ya NO le pasamos el onSuccess, porque ahora lo hace la tarjeta emergente
                        viewModel.registrar(nuevoUsuario)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Registrarse", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            } // <-- Aquí termina el Column del formulario

            // 👇 AQUÍ ESTÁ LA TARJETA EMERGENTE (ALERT DIALOG)
            if (viewModel.mostrarDialogoExito) {
                AlertDialog(
                    onDismissRequest = { /* Se deja vacío para que no puedan cerrarla picando fuera */ },
                    title = {
                        Text(text = "¡Registro Exitoso! 🎉", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text("Tu cuenta ha sido creada correctamente. Ahora puedes iniciar sesión en la aplicación.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.mostrarDialogoExito = false // 1. Apagamos la alerta
                                onBack() // 2. Viajamos al Login 🚀
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro)
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    }
                )
            }

        } // <-- Aquí termina el Box
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    label: String,
    opciones: List<String>,
    seleccionActual: String,
    onSeleccionCambiada: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = { expandido = !expandido },
    ) {
        OutlinedTextField(
            value = seleccionActual,
            onValueChange = {},
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ColorFondoInput,
                unfocusedContainerColor = ColorFondoInput,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
                    readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .menuAnchor(),
            shape = RoundedCornerShape(8.dp)
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion,color = Color.Gray, fontSize = 14.sp) },
                    onClick = {
                        onSeleccionCambiada(opcion)
                        expandido = false
                    },
                )
            }
        }
    }
}