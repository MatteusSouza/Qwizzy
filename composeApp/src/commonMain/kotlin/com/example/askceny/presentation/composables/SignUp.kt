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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.askceny.domain.types.AuthState
import com.example.askceny.presentation.viewmodels.AuthViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    signUpOnClick: () -> Unit,
    signInOnClick: () -> Unit,
) {

    val authState by  viewModel.authState.collectAsState()

    var displayName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val invalidEmail by viewModel.emailError.collectAsState()
    val invalidDisplayName by viewModel.displayNameError.collectAsState()
    val invalidPassword by viewModel.passwordError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
        OutlinedTextField(
            value = displayName,
            onValueChange = {it ->
                displayName = it
                viewModel.updateDisplayNameError()
            },
            label = { Text("Name") },
            isError = !invalidDisplayName.isEmpty(),
            supportingText = {
                if (!invalidDisplayName.isEmpty()) {
                    Text(invalidDisplayName)
                }
            },
            placeholder = { Text("Name") }
        )
        OutlinedTextField(
            value = email,
            onValueChange = {it ->
                email = it
                viewModel.updateEmailError()
            },
            isError = !invalidEmail.isEmpty(),
            supportingText = {
                if (!invalidEmail.isEmpty()) {
                    Text(invalidEmail)
                }
            },
            label = { Text("Email") },
            placeholder = { Text("email@example.com") }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {it ->
                password = it
                viewModel.updatePasswordError()
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") },
            placeholder = { Text("Password") },
            isError = !invalidPassword.isEmpty(),
            supportingText = {
                if (!invalidPassword.isEmpty())
                    Text(invalidPassword)
            }
        )
        Button(
            onClick = { viewModel.signUp(displayName, email, password) },
            //this enabled verification was not tested
            enabled = invalidDisplayName.isEmpty() && invalidEmail.isEmpty() && invalidPassword.isEmpty() && /*!displayName.isEmpty() && */ displayName.length >= 3 && email.isNotEmpty() && password.isNotEmpty(),
//            enabled = invalidDisplayName.isEmpty() && invalidEmail.isEmpty()
        ) {
            Text("Next")
        }
        TextButton(onClick = {
            signInOnClick()
        }) {
            Text("I already have an account")
        }

        if (authState is AuthState.Authenticated){
            signUpOnClick()
        }

        if (authState is AuthState.AuthError) {
            println("AUTH_SIGNUP_UPDATE: ;; ${(authState as AuthState.AuthError).errorCode}")
            println("AUTH_SIGNUP_UPDATE: ;; displayNameError: '$invalidDisplayName' emailError: '$invalidEmail' passwordError: '$invalidPassword'")
        }else {
            println("AUTH_SIGNUP_UPDATE: .. $authState")
            println("AUTH_SIGNUP_UPDATE: .. displayNameError: '$invalidDisplayName' emailError: '$invalidEmail' passwordError: '$invalidPassword'")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpPreview() {
//    SignUpScreen(modifier = Modifier, AuthViewModel(AuthRepositoryFake()), { }, { })
}
