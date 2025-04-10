package com.example.askceny.exceptions

import com.example.askceny.data.types.ErrorCode

class AuthException(val authException: ErrorCode) : IllegalArgumentException(authException.name)