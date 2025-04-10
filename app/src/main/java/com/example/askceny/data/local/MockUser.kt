package com.example.askceny.data.local

import com.example.askceny.data.models.User

data class MockUser(
    val id: String,
    val displayName: String,
    var username: String,
    var email: String,
    var about: String,
    var website: String,
    var password: String
) {
    fun toUser() : User {
        return User(
            id = this.id,
            username = this.username,
            email = this.email,
            displayName = this.displayName,
            about = this.about,
            website = this.website
        )
    }
}