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
import com.example.askceny.presentation.state.SignUpUiState
import com.example.askceny.presentation.viewmodels.AuthViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    signUpOnClick: () -> Unit,
    signInOnClick: () -> Unit,
    onEmailConfirmationRequired: (String) -> Unit,
) {
    val authState by viewModel.authState.collectAsState()
    var displayName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val invalidEmail by viewModel.emailError.collectAsState()
    val invalidDisplayName by viewModel.displayNameError.collectAsState()
    val invalidPassword by viewModel.passwordError.collectAsState()

    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Authenticated -> signUpOnClick()
            is AuthState.EmailConfirmationRequired -> onEmailConfirmationRequired(state.email)
            else -> Unit
        }
    }

    SignUpContent(
        modifier = modifier,
        state = SignUpUiState(
            displayName = displayName,
            email = email,
            password = password,
            displayNameError = invalidDisplayName,
            emailError = invalidEmail,
            passwordError = invalidPassword,
            isLoading = authState is AuthState.Loading,
            isAuthenticated = authState is AuthState.Authenticated,
        ),
        onDisplayNameChange = {
            displayName = it
            viewModel.updateDisplayNameError()
        },
        onEmailChange = {
            email = it
            viewModel.updateEmailError()
        },
        onPasswordChange = {
            password = it
            viewModel.updatePasswordError()
        },
        onSubmit = { viewModel.signUp(displayName, email, password) },
        onSignInClick = signInOnClick,
    )

    if (authState is AuthState.AuthError) {
        println("AUTH_SIGNUP_UPDATE: ;; ${(authState as AuthState.AuthError).errorCode}")
        println("AUTH_SIGNUP_UPDATE: ;; displayNameError: '$invalidDisplayName' emailError: '$invalidEmail' passwordError: '$invalidPassword'")
    } else {
        println("AUTH_SIGNUP_UPDATE: .. $authState")
        println("AUTH_SIGNUP_UPDATE: .. displayNameError: '$invalidDisplayName' emailError: '$invalidEmail' passwordError: '$invalidPassword'")
    }
}
