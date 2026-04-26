package com.example.askceny.data.local

import androidx.room.Room
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getDatabaseBuilder() = Room.databaseBuilder<AppDatabase>(
    name = "${documentDirectory()}/$DATABASE_NAME",
)

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    return checkNotNull(
        NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )?.path
    )
}
