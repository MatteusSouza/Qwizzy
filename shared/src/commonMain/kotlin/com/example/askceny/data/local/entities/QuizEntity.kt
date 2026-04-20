package com.example.askceny.data.local.entities

import androidx.room.Entity

@Entity
data class QuizEntity(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val img: String = "",
    val isPublic: Boolean = false
)
