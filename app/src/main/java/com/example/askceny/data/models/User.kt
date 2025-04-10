package com.example.askceny.data.models

import java.util.UUID

data class User(
    val id: String,
    var displayName: String,
    var username: String,
    var email: String,
    var about: String,
    var website: String
    )