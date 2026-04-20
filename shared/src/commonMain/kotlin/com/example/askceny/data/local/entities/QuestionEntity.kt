package com.example.askceny.data.local.entities

import androidx.room.Entity

@Entity
data class QuestionEntity(
    val id: String = "",
    val text: String = "",
    val type: String = "",
    val quizId: String = ""
)
