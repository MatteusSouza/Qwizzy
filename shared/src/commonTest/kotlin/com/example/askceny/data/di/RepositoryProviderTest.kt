package com.example.askceny.data.di

import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.data.repositories.AuthRepositoryImpl
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs

class RepositoryProviderTest {
    @Test
    fun `production supabase auth remote data source does not use placeholder client`() {
        val remoteDataSource = SupabaseAuthRemoteDataSource()

        assertFalse(remoteDataSource.isUsingPlaceholderClient)
    }

    @Test
    fun `repository provider auth repository does not use placeholder client`() {
        val repository: AuthRepository = RepositoryProvider.authRepository
        val authRepository = assertIs<AuthRepositoryImpl>(repository)

        assertFalse(authRepository.isUsingPlaceholderAuthClient)
    }
}
