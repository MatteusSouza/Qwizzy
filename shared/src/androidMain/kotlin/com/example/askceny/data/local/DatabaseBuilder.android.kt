package com.example.askceny.data.local

import androidx.room.RoomDatabase
import androidx.room.driver.AndroidSQLiteDriver

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
    val driver = AndroidSQLiteDriver()
    return RoomDatabase.builder<AppDatabase>(driver)
}
