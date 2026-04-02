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
import com.example.integrador_sigrad.viewmodel.AuthViewModel

@Composable
fun MainScreen(authViewModel: AuthViewModel,
               onCerrarSesion: () -> Unit ) { // ✅ Recibe el authViewModel
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(paddingValues),
            authViewModel = authViewModel, // ✅ Lo pasa al NavGraph
            onCerrarSesion = onCerrarSesion
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
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

fun navegarSeguro(navController: NavHostController, ruta: String) {
    navController.navigate(ruta) {
        popUpTo(navController.graph.startDestinationId) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}