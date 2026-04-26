package com.example.askceny.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuestionEntity(
    @PrimaryKey
    val id: String = "",
    val text: String = "",
    val type: String = "",
    val quizId: String = ""
)
