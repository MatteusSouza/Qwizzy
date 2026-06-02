package com.example.askceny.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
actual fun isLandscape(): Boolean {
    val containerSize = LocalWindowInfo.current.containerSize
    return containerSize.width > containerSize.height
}
