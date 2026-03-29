package com.example.integrador_sigrad.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.integrador_sigrad.ui.components.CustomTextField

// ⚠️ IMPORTANTE: Si pusiste el CustomTextField en la carpeta 'components',
// descomenta la siguiente línea para que no te marque error:
// import com.example.integrador_sigrad.ui.components.CustomTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecuperarPasswordScreen(onBack: () -> Unit) {
    // Variable para guardar lo que el usuario escribe
    var correo by remember { mutableStateOf("") }

    // El color azul oscuro de tu diseño
    val ColorBotonOscuro = Color(0xFF344356)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restablecer contraseña", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Ingresa tu correo electrónico",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Te enviaremos un enlace para restablecer tu contraseña.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reutilizamos el input gris que creaste para el Login
            CustomTextField(
                label = "Correo:",
                placeholder = "Correo electrónico",
                value = correo,
                onValueChange = { correo = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de enviar
            Button(
                onClick = { /* Aquí luego conectaremos con Spring Boot para enviar el correo */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorBotonOscuro)
            ) {
                Text(
                    text = "Enviar enlace",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}