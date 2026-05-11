package com.example.askceny.domain.types

sealed class AuthState {
    object Loading: AuthState()
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    data class EmailConfirmationRequired(val email: String): AuthState()
    data class AuthError(val errorCode: ErrorCode): AuthState()
}
