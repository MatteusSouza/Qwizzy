package com.example.askceny.data.local.entities

import androidx.room.Entity

@Entity
data class AnswerEntity(
    val id: String = "",
    val text: String = "",
    val img: String = "",
    val questionId: String = ""
)
