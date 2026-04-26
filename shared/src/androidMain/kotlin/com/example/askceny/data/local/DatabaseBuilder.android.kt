package com.example.askceny.data.local

import android.content.Context
import androidx.room.Room

private lateinit var storedApplicationContext: Context

fun setDatabaseBuilderContext(context: Context) {
    storedApplicationContext = context.applicationContext
}

actual fun getDatabaseBuilder() = Room.databaseBuilder<AppDatabase>(
    context = applicationContext,
    name = applicationContext.getDatabasePath(DATABASE_NAME).absolutePath,
)

private val applicationContext: Context
    get() {
        check(::storedApplicationContext.isInitialized) {
            "DatabaseBuilder context must be initialized before requesting the Room builder."
        }
        return storedApplicationContext
    }
