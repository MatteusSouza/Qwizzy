package com.example.askceny.domain.exceptions

import com.example.askceny.domain.types.ErrorCode

class AuthException(val authException: ErrorCode) : IllegalArgumentException(authException.name)
