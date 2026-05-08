package com.example.askceny.data.di

import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthSessionClientFactory
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.repositories.QuizRepository
import com.example.askceny.data.repositories.AuthRepositoryImpl
import com.example.askceny.data.repositories.QuizRepositoryImpl

object RepositoryProvider {
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            SupabaseAuthRemoteDataSource(
                SupabaseAuthSessionClientFactory.create(),
            ),
        )
    }
    val quizRepository: QuizRepository = QuizRepositoryImpl()
}
