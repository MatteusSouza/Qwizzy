package com.example.askceny.data.di

import com.example.askceny.data.local.AppDatabase
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

expect val platformDatabaseModule: Module

fun initKoin(config: KoinAppDeclaration? = null): KoinApplication {
    return startKoin {
        config?.invoke(this)
        modules(databaseModule, platformDatabaseModule)
    }
}

fun initKoinIos(): KoinApplication = initKoin()
