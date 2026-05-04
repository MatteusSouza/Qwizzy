package com.example.askceny.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.askceny.domain.types.AuthState
import com.example.askceny.presentation.state.SignInUiState
import com.example.askceny.presentation.viewmodels.AuthViewModel

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    signInOnClick: () -> Unit,
    signUpOnClick: () -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by viewModel.passwordError.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            signInOnClick()
        }
    }

    SignInContent(
        modifier = modifier,
        state = SignInUiState(
            email = email,
            password = password,
            emailError = emailError,
            passwordError = passwordError,
            isLoading = authState is AuthState.Loading,
            isAuthenticated = authState is AuthState.Authenticated,
        ),
        onEmailChange = {
            email = it
            viewModel.updateEmailError()
            viewModel.updatePasswordError()
        },
        onPasswordChange = {
            password = it
            if (passwordError.isNotEmpty()) {
                viewModel.updatePasswordError()
                viewModel.updateEmailError()
            }
        },
        onSubmit = { viewModel.signIn(email, password) },
        onSignUpClick = signUpOnClick,
    )

    if (authState is AuthState.AuthError) {
        println("AUTH_LOGIN_COLUMN_UPDATE: ;; ${(authState as AuthState.AuthError).errorCode}")
        println("AUTH_LOGIN_COLUMN_UPDATE: ;; emailError: '$emailError' passwordError: '$passwordError'")
    } else {
        println("AUTH_LOGIN_COLUMN_UPDATE: .. $authState")
        println("AUTH_LOGIN_COLUMN_UPDATE: .. emailError: '$emailError' passwordError: '$passwordError'")
    }
}
