package com.example.integrador_sigrad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.integrador_sigrad.ui.components.MainScreen
import com.example.integrador_sigrad.ui.screens.LoginScreen
import com.example.integrador_sigrad.ui.screens.RecuperarPasswordScreen
import com.example.integrador_sigrad.ui.screens.RegistroScreen
import com.example.integrador_sigrad.ui.theme.Integrador_SIGRADTheme
import com.example.integrador_sigrad.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Integrador_SIGRADTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val rootNavController = rememberNavController()

                    // UN SOLO authViewModel para toda la app
                    val authViewModel: AuthViewModel = viewModel()

                    // ✅ PASO 3: Verificamos si hay sesión guardada en la memoria
                    // Si el usuario existe, saltamos a "main". Si no, pedimos "login".
                    val destinoInicial = if (authViewModel.usuarioLogueado != null) "main" else "login"

                    NavHost(
                        navController = rootNavController,
                        startDestination = destinoInicial // ✅ Aquí usamos la variable que decide
                    ) {
                        composable("login") {
                            LoginScreen(
                                authViewModel = authViewModel,
                                onNavigateToRegistro = { rootNavController.navigate("registro") },
                                onNavigateToRecuperar = { rootNavController.navigate("recuperar") },
                                onLoginSuccess = {
                                    rootNavController.navigate("main") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("registro") {
                            RegistroScreen(
                                onBack = { rootNavController.popBackStack() }
                            )
                        }

                        composable("recuperar") {
                            RecuperarPasswordScreen(
                                onBack = { rootNavController.popBackStack() }
                            )
                        }

                        composable("main") {
                            // Pasamos el mismo authViewModel a MainScreen
                            MainScreen(authViewModel = authViewModel,
                                onCerrarSesion = {
                                    rootNavController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                })
                        }
                    }
                }
            }
        }
    }
}