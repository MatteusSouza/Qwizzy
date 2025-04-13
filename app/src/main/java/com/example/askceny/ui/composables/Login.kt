package com.example.askceny.ui.composables

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.askceny.data.repositories.AuthRepositoryFake
import com.example.askceny.data.types.AuthState
import com.example.askceny.ui.viewmodels.AuthViewModel


@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel,
    signInOnClick: () -> Unit,
    signUpOnClick: () -> Unit
) {

    val authState by  viewModel.authState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val emailError by viewModel.emailError.collectAsState()
    val passwordError by  viewModel.passwordError.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),

        ) {
        OutlinedTextField(
            value = email,
            onValueChange = {it ->
                email = it
                viewModel.updateEmailError()
                viewModel.updatePasswordError()
            },
            label = { Text("Email") },
            placeholder = { Text("email@example.com") },
            isError = !emailError.isEmpty(),
            supportingText = {
                if (!emailError.isEmpty())
                    Text(emailError)
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = {it ->
                password = it
                if (passwordError.isNotEmpty()) {
                    viewModel.updatePasswordError()
                    viewModel.updateEmailError()
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Password") },
            placeholder = { Text("Password") },
            isError = !passwordError.isEmpty(),
            supportingText = {
                if (!passwordError.isEmpty())
                    Text(passwordError)
            }
        )
        Button(
            onClick = {
                viewModel.signIn(email, password)
            },
            enabled = emailError.isEmpty() && passwordError.isEmpty() && !email.isEmpty() && !password.isEmpty()  //It was not tested with wrong password
        )
        {
            Text(text = "Sign In")
        }
        Text("or")
        TextButton(onClick = {
            signUpOnClick()
        }) {
            Text("Create account")
        }
    }

    if (authState is AuthState.Authenticated){
        signInOnClick()
    }

    if (authState is AuthState.AuthError) {
        println("AUTH_LOGIN_COLUMN_UPDATE: ;; ${(authState as AuthState.AuthError).errorCode}")
        println("AUTH_LOGIN_COLUMN_UPDATE: ;; emailError: '$emailError' passwordError: '$passwordError'")
    }else {
        println("AUTH_LOGIN_COLUMN_UPDATE: .. $authState")
        println("AUTH_LOGIN_COLUMN_UPDATE: .. emailError: '$emailError' passwordError: '$passwordError'")
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    SignInScreen(modifier = Modifier,
        viewModel = AuthViewModel(AuthRepositoryFake()),
        { },
        { }
    )
}