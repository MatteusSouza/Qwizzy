package com.example.askceny.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.askceny.domain.types.AuthState
import com.example.askceny.presentation.preview.SampleUiState
import com.example.askceny.presentation.state.VerifyEmailUiState
import com.example.askceny.presentation.viewmodels.AuthViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun VerifyEmailScreen(
    email: String,
    viewModel: AuthViewModel,
    onVerified: () -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val authState by viewModel.authState.collectAsState()
    val otpError by viewModel.otpError.collectAsState()
    val otpInfo by viewModel.otpInfo.collectAsState()
    var code by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) {
            onVerified()
        }
    }

    VerifyEmailContent(
        modifier = modifier,
        state = VerifyEmailUiState(
            email = email,
            code = code,
            otpError = otpError,
            otpInfo = otpInfo,
            isLoading = authState is AuthState.Loading,
        ),
        onCodeChange = {
            code = it
            viewModel.updateOtpError()
        },
        onVerifyClick = { viewModel.verifyEmailOtp(email, code) },
        onResendClick = { viewModel.resendSignUpEmailOtp(email) },
        onBackToSignIn = onBackToSignIn,
    )
}

@Composable
fun VerifyEmailContent(
    state: VerifyEmailUiState,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackToSignIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Verify your email",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "We've sent a verification code to ${maskEmail(state.email)}.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the code to verify your email and finish signing in.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = state.code,
            onValueChange = onCodeChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Verification code") },
            placeholder = { Text("123456") },
            singleLine = true,
            isError = state.otpError.isNotEmpty(),
            supportingText = {
                when {
                    state.otpError.isNotEmpty() -> Text(state.otpError)
                    state.otpInfo.isNotEmpty() -> Text(state.otpInfo)
                }
            },
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onVerifyClick,
            enabled = state.canVerify,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Verify")
        }
        TextButton(
            onClick = onResendClick,
            enabled = !state.isLoading,
        ) {
            Text("Resend code")
        }
        TextButton(
            onClick = onBackToSignIn,
            enabled = !state.isLoading,
        ) {
            Text("Back to sign in")
        }
    }
}

internal fun maskEmail(email: String): String {
    val trimmedEmail = email.trim()
    val parts = trimmedEmail.split("@", limit = 2)
    if (parts.size != 2) return trimmedEmail

    val local = parts[0]
    val domain = parts[1]
    val domainParts = domain.split(".")
    val domainName = domainParts.firstOrNull().orEmpty()
    val suffix = domainParts.drop(1).joinToString(".")
    val maskedLocal = "${local.take(1)}***"
    val maskedDomain = "${domainName.take(1)}****"

    return if (suffix.isBlank()) {
        "$maskedLocal@$maskedDomain"
    } else {
        "$maskedLocal@$maskedDomain.$suffix"
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyEmailContentPreview() {
    VerifyEmailContent(
        state = SampleUiState.verifyEmail,
        onCodeChange = {},
        onVerifyClick = {},
        onResendClick = {},
        onBackToSignIn = {},
    )
}
