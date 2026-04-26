package com.example.askceny.data.di

import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.repositories.QuizRepository
import com.example.askceny.data.repositories.AuthRepositoryImpl
import com.example.askceny.data.repositories.QuizRepositoryImpl

object RepositoryProvider {
    val authRepository: AuthRepository = AuthRepositoryImpl()
    val quizRepository: QuizRepository = QuizRepositoryImpl()
}
