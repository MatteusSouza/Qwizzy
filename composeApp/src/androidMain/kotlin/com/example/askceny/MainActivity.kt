package com.example.askceny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.askceny.data.di.RepositoryProvider
import com.example.askceny.presentation.viewmodels.AuthViewModel
import com.example.askceny.presentation.viewmodels.QuizViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authRepository = RepositoryProvider.authRepository
        val quizRepository = RepositoryProvider.quizRepository

        val viewModelStoreOwner: ViewModelStoreOwner = this
        val authViewModel: AuthViewModel = ViewModelProvider.create(
            viewModelStoreOwner,
            factory = AuthViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(AuthViewModel.USER_REPOSITORY_KEY, authRepository)
            },
        )[AuthViewModel::class]

        val quizViewModel: QuizViewModel = ViewModelProvider.create(
            viewModelStoreOwner,
            factory = QuizViewModel.Factory,
            extras = MutableCreationExtras().apply {
                set(QuizViewModel.QUIZ_REPOSITORY_KEY, quizRepository)
            },
        )[QuizViewModel::class]

        setContent {
            App(authViewModel = authViewModel, quizViewModel = quizViewModel)
        }
    }
}
