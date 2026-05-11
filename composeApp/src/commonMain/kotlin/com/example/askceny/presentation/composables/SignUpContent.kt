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
import com.example.askceny.presentation.state.SignUpUiState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SignUpContent(
    state: SignUpUiState,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onSignInClick: () -> Unit,
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
            value = state.displayName,
            onValueChange = onDisplayNameChange,
            label = { Text("Name") },
            isError = state.displayNameError.isNotEmpty(),
            supportingText = {
                if (state.displayNameError.isNotEmpty()) {
                    Text(state.displayNameError)
                }
            },
            placeholder = { Text("Name") },
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            isError = state.emailError.isNotEmpty(),
            supportingText = {
                if (state.emailError.isNotEmpty()) {
                    Text(state.emailError)
                }
            },
            label = { Text("Email") },
            placeholder = { Text("email@example.com") },
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
            Text("Next")
        }
        TextButton(onClick = onSignInClick) {
            Text("I already have an account")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpContentPreview() {
    SignUpContent(
        state = SampleUiState.signUp,
        onDisplayNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onSubmit = {},
        onSignInClick = {},
        passwordVisible = false,
        onPasswordVisibilityChange = {},
    )
}
