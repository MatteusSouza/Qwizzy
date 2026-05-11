package com.example.askceny.data.di

import com.example.askceny.data.remote.api.AuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.data.repositories.AuthRepositoryImpl
import com.example.askceny.data.repositories.QuizRepositoryImpl
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.repositories.QuizRepository
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinModuleTest {
    @AfterTest
    fun tearDown() {
        runCatching { stopKoin() }
    }

    @Test
    fun `production supabase auth remote data source does not use placeholder client`() {
        val remoteDataSource = SupabaseAuthRemoteDataSource()

        assertFalse(remoteDataSource.isUsingPlaceholderClient)
    }

    @Test
    fun `koin resolves auth repository without placeholder client`() {
        val koin = startKoin {
            modules(remoteModule, repositoryModule)
        }.koin

        val repository: AuthRepository = koin.get()
        val authRepository = assertIs<AuthRepositoryImpl>(repository)

        assertFalse(authRepository.isUsingPlaceholderAuthClient)
    }

    @Test
    fun `koin resolves quiz repository`() {
        val koin = startKoin {
            modules(remoteModule, repositoryModule)
        }.koin

        val repository: QuizRepository = koin.get()

        assertIs<QuizRepositoryImpl>(repository)
    }

    @Test
    fun `koin resolves auth remote data source`() {
        val koin = startKoin {
            modules(remoteModule)
        }.koin

        val remoteDataSource: AuthRemoteDataSource = koin.get()

        assertIs<SupabaseAuthRemoteDataSource>(remoteDataSource)
    }
}
