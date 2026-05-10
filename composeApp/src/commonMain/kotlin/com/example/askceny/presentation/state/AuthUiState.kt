package com.example.askceny.presentation.state

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
) {
    val canSubmit: Boolean
        get() = emailError.isEmpty() &&
            passwordError.isEmpty() &&
            email.isNotEmpty() &&
            password.isNotEmpty()
}

data class SignUpUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val displayNameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
) {
    val canSubmit: Boolean
        get() = displayNameError.isEmpty() &&
            emailError.isEmpty() &&
            passwordError.isEmpty() &&
            displayName.length >= 3 &&
            email.isNotEmpty() &&
            password.isNotEmpty()
}

data class VerifyEmailUiState(
    val email: String = "",
    val code: String = "",
    val otpError: String = "",
    val otpInfo: String = "",
    val isLoading: Boolean = false,
) {
    val canVerify: Boolean
        get() = code.isNotBlank() && !isLoading
}
