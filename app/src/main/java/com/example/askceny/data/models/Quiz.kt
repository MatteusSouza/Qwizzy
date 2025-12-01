package com.example.askceny.data.models

import com.google.firebase.firestore.DocumentId

data class Quiz(
    @DocumentId val id: String = "",
    var title: String = "",
    var description: String = "",
    var img: String = "",
    var isPublic: Boolean = false
)
