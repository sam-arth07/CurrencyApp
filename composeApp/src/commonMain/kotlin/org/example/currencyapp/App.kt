package org.example.currencyapp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.example.currencyapp.di.initializeKoin
import org.example.currencyapp.presentation.screens.HomeScreen

@Composable
fun App() {
//    val colors = if (!isSystemInDarkTheme()) {
//        lightScheme
//    } else {
//        darkScheme
//    }
    initializeKoin()
    MaterialTheme {
        Navigator(HomeScreen())
    }
}