package com.example.askceny.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey
    val id: String = "",
    val displayName: String = "",
    val username: String = "",
    val email: String = "",
    val about: String = "",
    val website: String = ""
)
