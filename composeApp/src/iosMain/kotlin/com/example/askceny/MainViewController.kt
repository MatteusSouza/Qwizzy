package com.example.askceny

import androidx.compose.ui.window.ComposeUIViewController
import com.example.askceny.data.di.RepositoryProvider
import com.example.askceny.data.di.initKoinIos
import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel

private var isKoinStarted = false

class KoinHelper {
    fun start() {
        if (!isKoinStarted) {
            initKoinIos()
            isKoinStarted = true
        }
    }
}

fun MainViewController() = ComposeUIViewController {
    App(
        authViewModel = AuthViewModel(RepositoryProvider.authRepository),
        quizViewModel = QuizViewModel(RepositoryProvider.quizRepository),
    )
}
