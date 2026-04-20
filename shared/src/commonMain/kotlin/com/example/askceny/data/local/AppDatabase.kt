package com.example.askceny.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.askceny.data.local.dao.QuestionDao
import com.example.askceny.data.local.dao.QuizDao
import com.example.askceny.data.local.dao.UserDao
import com.example.askceny.data.local.entities.AnswerEntity
import com.example.askceny.data.local.entities.QuestionEntity
import com.example.askceny.data.local.entities.QuizEntity
import com.example.askceny.data.local.entities.UserEntity

@Database(
    entities = [UserEntity::class, QuizEntity::class, QuestionEntity::class, AnswerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun quizDao(): QuizDao
    abstract fun questionDao(): QuestionDao
}
