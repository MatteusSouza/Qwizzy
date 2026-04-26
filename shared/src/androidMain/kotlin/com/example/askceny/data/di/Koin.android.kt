package com.example.askceny.data.di

import com.example.askceny.data.local.AppDatabase
import com.example.askceny.data.local.getRoomDatabase
import org.koin.dsl.module

actual val platformDatabaseModule = module {
    single<AppDatabase> { getRoomDatabase() }
}
