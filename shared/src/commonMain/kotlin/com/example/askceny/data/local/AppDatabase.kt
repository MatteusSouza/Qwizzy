package com.example.askceny.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.example.askceny.data.local.dao.QuestionDao
import com.example.askceny.data.local.dao.QuizDao
import com.example.askceny.data.local.dao.UserDao
import com.example.askceny.data.local.entities.AnswerEntity
import com.example.askceny.data.local.entities.QuestionEntity
import com.example.askceny.data.local.entities.QuizEntity
import com.example.askceny.data.local.entities.UserEntity
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [UserEntity::class, QuizEntity::class, QuestionEntity::class, AnswerEntity::class],
    version = 1,
    exportSchema = false
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

const val DATABASE_NAME = "askceny.db"

fun createRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>,
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}
