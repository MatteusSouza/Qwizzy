package com.example.askceny.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
actual fun isLandscape(): Boolean {
    val containerSize = LocalWindowInfo.current.containerSize
    return containerSize.width > containerSize.height
}
