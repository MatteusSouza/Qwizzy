package com.example.askceny.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.SignInUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SignInContent(
    state: SignInUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSignUpClick: () -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            placeholder = { Text("email@example.com") },
            isError = state.emailError.isNotEmpty(),
            supportingText = {
                if (state.emailError.isNotEmpty()) {
                    Text(state.emailError)
                }
            },
        )
        PasswordTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = onPasswordVisibilityChange,
            isError = state.passwordError.isNotEmpty(),
            supportingText = {
                if (state.passwordError.isNotEmpty()) {
                    Text(state.passwordError)
                }
            },
        )
        Button(
            onClick = onSubmit,
            enabled = state.canSubmit,
        ) {
            Text(text = "Sign In")
        }
        Text("or")
        TextButton(onClick = onSignUpClick) {
            Text("Create account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInContentPreview() {
    SignInContent(
        state = SampleUiState.signIn,
        onEmailChange = {},
        onPasswordChange = {},
        onSubmit = {},
        onSignUpClick = {},
        passwordVisible = false,
        onPasswordVisibilityChange = {},
    )
}
