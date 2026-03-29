package com.example.integrador_sigrad.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.integrador_sigrad.navigation.AppNavGraph
import com.example.integrador_sigrad.navigation.RutasNavegacion

/**
 * Pantalla principal que contiene el Scaffold con la barra de navegación inferior
 * y el grafo de navegación de la aplicación.
 */
@Composable
fun MainScreen() {
    // Controlador de navegación que gestiona la navegación entre pantallas
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        // Grafo de navegación que contiene todas las pantallas
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

/**
 * Barra de navegación inferior que permite navegar entre las secciones principales
 * de la aplicación (Inicio, Áreas, Historial, Perfil).
 * 
 * @param navController Controlador de navegación para gestionar los cambios de pantalla
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // Obtenemos la ruta actual para resaltar el botón correspondiente
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Botón 1: Inicio
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = rutaActual == RutasNavegacion.INICIO,
            onClick = { navegarSeguro(navController, RutasNavegacion.INICIO) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF3F4F6)
            )
        )
        
        // Botón 2: Áreas (también se selecciona cuando estamos en detalle o formulario)
        NavigationBarItem(
            icon = { Icon(Icons.Default.DateRange, contentDescription = "Áreas") },
            label = { Text("Áreas") },
            selected = rutaActual == RutasNavegacion.AREAS 
                    || rutaActual?.startsWith("detalle") == true 
                    || rutaActual?.startsWith("formulario") == true,
            onClick = { navegarSeguro(navController, RutasNavegacion.AREAS) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF3F4F6)
            )
        )
        
        // Botón 3: Historial
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Historial") },
            label = { Text("Historial") },
            selected = rutaActual == RutasNavegacion.HISTORIAL,
            onClick = { navegarSeguro(navController, RutasNavegacion.HISTORIAL) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF3F4F6)
            )
        )
        
        // Botón 4: Perfil
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
            label = { Text("Perfil") },
            selected = rutaActual == RutasNavegacion.PERFIL,
            onClick = { navegarSeguro(navController, RutasNavegacion.PERFIL) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = Color.Black,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFFF3F4F6)
            )
        )
    }
}

/**
 * Función auxiliar para realizar navegación segura evitando duplicados en el stack.
 * Mantiene el estado de las pantallas y evita crear múltiples instancias de la misma pantalla.
 * 
 * @param navController Controlador de navegación
 * @param ruta Ruta destino a la que navegar
 */
fun navegarSeguro(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}