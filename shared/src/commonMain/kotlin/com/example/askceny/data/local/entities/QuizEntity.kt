package com.example.askceny.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class QuizEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val img: String = "",
    val isPublic: Boolean = false
)
