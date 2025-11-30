package com.example.askceny.data.types

enum class ErrorCode {
    INVALID_EMAIL,
    ERROR_INVALID_CREDENTIAL,
    USER_DISABLED,
    EMAIL_ALREADY_IN_USE,
    WEAK_PASSWORD,
    NETWORK_ERROR,
    ERROR_TOO_MANY_REQUESTS,
    UNKNOWN_ERROR,
    UNAUTHENTICATED
//    USER_NOT_FOUND,
//    WRONG_PASSWORD,
}

/** TODO: Implement FirebaseFirestore ErrorCode
 * UNAVAILABLE
 * NOT_FOUND
 * ALREADY_EXISTS
 * CANCELLED
 * */