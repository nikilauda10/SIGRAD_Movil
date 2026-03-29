package com.example.integrador_sigrad.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.integrador_sigrad.ui.components.CustomTextField
import com.example.integrador_sigrad.ui.theme.ColorBotonOscuro
import com.example.integrador_sigrad.ui.theme.ColorFondoInput
import com.example.integrador_sigrad.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegistro: () -> Unit,
    onNavigateToRecuperar: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(text = "Iniciar Sesión", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(24.dp))

        CustomTextField(label = "Correo:", placeholder = "Correo electrónico", value = correo, onValueChange = { correo = it })


        CustomPasswordField(
            value = password,
            onValueChange = { password = it }
        )

        // Mensaje de error dinámico
        if (viewModel.errorMessage != null) {
            Text(
                text = viewModel.errorMessage!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.login(correo, password, onLoginSuccess) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = "Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToRegistro) {
            Text(text = "¿No tienes una cuenta? \nRegístrate", color = Color.Gray, textAlign = TextAlign.Center)
        }

        TextButton(onClick = onNavigateToRecuperar) {
            Text(text = "Restablecer Contraseña", color = Color.Gray)
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit
) {
    // Esta variable guarda si el ojito está abierto o cerrado
    var passwordVisible by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = "Contraseña", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("Contraseña:", color = Color.Gray, fontSize = 14.sp) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = ColorFondoInput,
                unfocusedContainerColor = ColorFondoInput,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            // Si está visible no hace nada, si está oculto le pone los puntitos
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    // Cambia el emoji dependiendo del estado
                    Text(
                        text = if (passwordVisible) "🙈" else "👁️",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            shape = RoundedCornerShape(8.dp)
        )
    }
}