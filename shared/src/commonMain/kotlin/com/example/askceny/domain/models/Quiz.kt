package com.example.askceny.domain.models

data class Quiz(
    val id: String = "",
    var title: String = "",
    var description: String = "",
    var img: String = "",
    var isPublic: Boolean = false
)
