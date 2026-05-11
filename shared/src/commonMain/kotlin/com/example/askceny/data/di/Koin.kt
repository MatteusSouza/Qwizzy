package com.example.askceny.data.di

import com.example.askceny.data.local.AppDatabase
import com.example.askceny.data.remote.api.AuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthRemoteDataSource
import com.example.askceny.data.remote.api.SupabaseAuthSessionClient
import com.example.askceny.data.remote.api.SupabaseAuthSessionClientFactory
import com.example.askceny.data.repositories.AuthRepositoryImpl
import com.example.askceny.data.repositories.QuizRepositoryImpl
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.repositories.QuizRepository
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val databaseModule = module {
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().quizDao() }
    single { get<AppDatabase>().questionDao() }
}

internal val remoteModule = module {
    single<SupabaseAuthSessionClient> { SupabaseAuthSessionClientFactory.create() }
    single<AuthRemoteDataSource> { SupabaseAuthRemoteDataSource(get()) }
}

internal val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<QuizRepository> { QuizRepositoryImpl() }
}

expect val platformDatabaseModule: Module

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(
            databaseModule,
            remoteModule,
            repositoryModule,
            platformDatabaseModule,
        )
    }
}
