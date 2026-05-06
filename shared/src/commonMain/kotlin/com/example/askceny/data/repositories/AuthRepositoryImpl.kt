package com.example.askceny.data.repositories

import com.example.askceny.data.remote.api.AuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.domain.models.User
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource = SupabaseAuthRemoteDataSource(),
) : AuthRepository {
    override suspend fun signUpWithEmail(displayName: String, email: String, password: String): AuthState {
        TODO("Supabase email sign-up not yet implemented")
    }

    override suspend fun signInWithEmail(email: String, password: String): AuthState {
        TODO("Supabase email sign-in not yet implemented")
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): AuthState {
        TODO("Supabase Google sign-up not yet implemented")
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): AuthState {
        TODO("Supabase Google sign-in not yet implemented")
    }

    override fun getCurrentUser(): User? {
        return remoteDataSource.getCurrentUser()
    }

    override fun signOut() {
        remoteDataSource.signOut()
    }
}
