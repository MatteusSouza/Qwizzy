package com.example.askceny.domain.repositories

import com.example.askceny.domain.models.User
import com.example.askceny.domain.types.AuthState

interface AuthRepository {
    suspend fun createUserWithEmailAndPassword(displayName: String, email: String, password: String) : AuthState
    suspend fun signInWithEmailAndPassword(email: String, password: String) : AuthState
    fun getCurrentUser() : User?
    fun signOut()
}
