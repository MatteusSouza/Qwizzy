package com.example.askceny.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AnswerEntity(
    @PrimaryKey
    val id: String = "",
    val text: String = "",
    val img: String = "",
    val questionId: String = ""
)
