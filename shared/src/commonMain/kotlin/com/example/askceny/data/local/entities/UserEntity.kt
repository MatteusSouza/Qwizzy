package com.example.askceny.data.local.entities

import androidx.room.Entity

@Entity
data class UserEntity(
    val id: String = "",
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val about: String = "",
    val website: String = ""
)
