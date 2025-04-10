package com.example.askceny.data.types

sealed class AuthState {
    object Loading: AuthState()
    object Authenticated: AuthState()
    object Unauthenticated: AuthState()
    data class AuthError(val errorCode: ErrorCode): AuthState()
}