package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var rol by remember { mutableStateOf("") } // Empieza vacío para forzar a elegir
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    label = "Nombre Completo *",
                    placeholder = "Ej. Jorge Moranchel",
                    value = nombre,
                    onValueChange = { nombre = it }
                )

                // ROL DINÁMICO DESDE EL BACKEND
                CustomDropdownMenu(
                    label = "Rol *",
                    opciones = viewModel.listaRoles,
                    seleccionActual = rol,
                    onSeleccionCambiada = { rol = it }
                )

                // ETIQUETA DINÁMICA DEPENDIENDO DEL ROL
                val esEstudiante = rol.uppercase() == "ESTUDIANTE" || rol.uppercase() == "ALUMNO"
                val etiquetaIdentificacion = if (esEstudiante) "Matrícula *" else "Código de Trabajador *"
                val placeholderIdentificacion = if (esEstudiante) "Ej. 20243DS051" else "Ej. 12345"

                // FORZAMOS MATRÍCULA A MAYÚSCULAS
                CustomTextField(
                    label = etiquetaIdentificacion,
                    placeholder = placeholderIdentificacion,
                    value = matricula,
                    onValueChange = { matricula = it.uppercase() }
                )

                // FORZAMOS CORREO A MINÚSCULAS
                CustomTextField(
                    label = "Correo Institucional *",
                    placeholder = "ejemplo@utez.edu.mx",
                    value = correo,
                    onValueChange = { correo = it.lowercase() }
                )

                // FORZAMOS TELÉFONO SOLO A NÚMEROS Y 10 DÍGITOS
                CustomTextField(
                    label = "Teléfono *",
                    placeholder = "10 dígitos numéricos",
                    value = telefono,
                    onValueChange = { newValue ->
                        val soloNumeros = newValue.filter { it.isDigit() }
                        if (soloNumeros.length <= 10) telefono = soloNumeros
                    }
                )

                // CARRERAS DINÁMICAS DESDE EL BACKEND
                CustomDropdownMenu(
                    label = "Carrera *",
                    opciones = viewModel.listaCarreras,
                    seleccionActual = carrera,
                    onSeleccionCambiada = { carrera = it }
                )

                CustomPasswordField(value = password, onValueChange = { password = it })

                // Mensaje de error dinámico y limpio
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
                            emailInstitucional = correo, // <--- LA LETRA I MAYÚSCULA CORREGIDA
                            carrera = carrera,
                            rol = rol,
                            contrasena = password,
                            telefono = telefono
                        )
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
            }

            // AQUÍ ESTÁ LA TARJETA EMERGENTE (ALERT DIALOG)
            if (viewModel.mostrarDialogoExito) {
                AlertDialog(
                    onDismissRequest = { /* Se deja vacío para que no puedan cerrarla picando fuera */ },
                    title = {
                        Text(text = "¡Registro Exitoso!", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text("Tu cuenta ha sido creada correctamente. Ahora puedes revisar tu correo para activarla e iniciar sesión.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.mostrarDialogoExito = false // Apagamos la alerta
                                onBack() // Viajamos al Login
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro)
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    }
                )
            }
        }
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
        onExpandedChange = { expandido = !expandido }
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
                    text = { Text(opcion, color = Color.Gray, fontSize = 14.sp) },
                    onClick = {
                        onSeleccionCambiada(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}