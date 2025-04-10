package com.example.askceny.data.models

data class Quiz(
    val id: String,
    var title: String,
    var description: String = "",
    var img: String = "",
    var isPublic: Boolean = false
)
