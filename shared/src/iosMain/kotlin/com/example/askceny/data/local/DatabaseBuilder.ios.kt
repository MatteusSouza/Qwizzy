package com.example.askceny.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    return RoomDatabase.builder<AppDatabase>(BundledSQLiteDriver())
}
