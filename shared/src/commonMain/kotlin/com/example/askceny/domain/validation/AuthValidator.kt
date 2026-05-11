package com.example.askceny.domain.validation

data class AuthValidationResult(
    val displayNameError: String = "",
    val emailError: String = "",
    val passwordError: String = "",
) {
    val isValid: Boolean
        get() = displayNameError.isEmpty() &&
            emailError.isEmpty() &&
            passwordError.isEmpty()
}

object AuthValidator {
    fun validateSignIn(email: String, password: String): AuthValidationResult {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        return AuthValidationResult(
            emailError = validateEmail(trimmedEmail),
            passwordError = if (trimmedPassword.isEmpty()) PASSWORD_REQUIRED else "",
        )
    }

    fun validateSignUp(displayName: String, email: String, password: String): AuthValidationResult {
        val trimmedDisplayName = displayName.trim()
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        return AuthValidationResult(
            displayNameError = if (trimmedDisplayName.isEmpty()) DISPLAY_NAME_REQUIRED else "",
            emailError = validateEmail(trimmedEmail),
            passwordError = if (trimmedPassword.isEmpty()) PASSWORD_REQUIRED else "",
        )
    }

    private fun validateEmail(email: String): String {
        return when {
            email.isEmpty() -> EMAIL_REQUIRED
            !EMAIL_PATTERN.matches(email) -> INVALID_EMAIL
            else -> ""
        }
    }

    private const val DISPLAY_NAME_REQUIRED = "Name can not be empty"
    private const val EMAIL_REQUIRED = "Email can not be empty"
    private const val INVALID_EMAIL = "Invalid email"
    private const val PASSWORD_REQUIRED = "Password can not be empty"
    private val EMAIL_PATTERN = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
}
