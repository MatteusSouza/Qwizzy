package com.example.askceny.presentation.di

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import org.koin.mp.KoinPlatformTools

@Composable
inline fun <reified VM : ViewModel> koinViewModel(): VM {
    val koin = KoinPlatformTools.defaultContext().get()
    val modelClass = VM::class
    val factory = remember(koin, modelClass) {
        viewModelFactory {
            initializer {
                koin.get<VM>()
            }
        }
    }

    return viewModel(factory = factory)
}
