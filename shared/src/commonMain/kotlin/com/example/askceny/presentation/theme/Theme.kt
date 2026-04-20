package com.example.askceny.presentation.theme

import androidx.compose.runtime.Composable

@Composable
expect fun AskCenyTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
)
