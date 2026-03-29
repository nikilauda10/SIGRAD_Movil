package com.example.integrador_sigrad

// Importa tus pantallas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.integrador_sigrad.ui.components.MainScreen
import com.example.integrador_sigrad.ui.screens.LoginScreen
import com.example.integrador_sigrad.ui.screens.RecuperarPasswordScreen
import com.example.integrador_sigrad.ui.screens.RegistroScreen
import com.example.integrador_sigrad.ui.theme.Integrador_SIGRADTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Hace que tu app ocupe toda la pantalla (hasta arriba donde está el reloj)
        setContent {
            Integrador_SIGRADTheme {
                // Un Surface es un contenedor básico que toma el color de fondo de tu tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Creamos el controlador principal
                    val rootNavController = rememberNavController()

                    // 2. Definimos las rutas principales de la app
                    NavHost(
                        navController = rootNavController,
                        startDestination = "login" // ¡La app arranca en el Login!
                    ) {

                        // Ruta de Login
                        composable("login") {
                            LoginScreen(
                                onNavigateToRegistro = { rootNavController.navigate("registro") },
                                onNavigateToRecuperar = { rootNavController.navigate("recuperar") },
                                onLoginSuccess = {
                                    // Si el login es exitoso, borramos el historial para que no pueda volver al login con el botón de "Atrás" y lo mandamos a Main
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // Ruta de Registro
                        composable("registro") {
                            RegistroScreen(
                                onBack = { rootNavController.popBackStack() } // Vuelve a la pantalla anterior
                            )
                        }

                        // Ruta de Recuperar Contraseña
                        composable("recuperar") {
                            RecuperarPasswordScreen(
                                onBack = { rootNavController.popBackStack() }
                            )
                        }

                        // Ruta del Mundo Principal (La que tiene la barra de navegación abajo)
                        composable("main") {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}