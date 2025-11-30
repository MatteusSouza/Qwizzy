package com.example.askceny.data.repositories

import com.example.askceny.data.models.User
import com.example.askceny.data.types.AuthState

interface AuthRepository {
    suspend fun createUserWithEmailAndPassword(displayName: String, email: String, password: String) : AuthState
    suspend fun signInWithEmailAndPassword(email: String, password: String) : AuthState
    fun getCurrentUser() : User?
    fun signOut()
}