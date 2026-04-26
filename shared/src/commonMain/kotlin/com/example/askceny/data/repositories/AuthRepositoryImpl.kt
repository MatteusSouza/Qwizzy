package com.example.askceny.data.repositories

import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState

class AuthRepositoryImpl : AuthRepository {
    override suspend fun createUserWithEmailAndPassword(displayName: String, email: String, password: String): AuthState {
        TODO("Not yet implemented")
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthState {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): User? {
        return User("", "", "", "", "", "")
        TODO("Not yet implemented")
    }

    override fun signOut() {
//        TODO("Not yet implemented")
    }
}
