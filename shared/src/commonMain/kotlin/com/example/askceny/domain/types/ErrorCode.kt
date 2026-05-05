package com.example.askceny.domain.types

enum class ErrorCode(val supabaseCode: String?) {
    INVALID_CREDENTIALS("invalid_credentials"),
    EMAIL_EXISTS("email_exists"),
    USER_ALREADY_EXISTS("user_already_exists"),
    WEAK_PASSWORD("weak_password"),
    VALIDATION_FAILED("validation_failed"),
    EMAIL_ADDRESS_INVALID("email_address_invalid"),
    EMAIL_PROVIDER_DISABLED("email_provider_disabled"),
    OVER_REQUEST_RATE_LIMIT("over_request_rate_limit"),
    EMAIL_NOT_CONFIRMED("email_not_confirmed"),
    SIGNUP_DISABLED("signup_disabled"),
    REQUEST_TIMEOUT("request_timeout"),
    NO_AUTHORIZATION("no_authorization"),
    SESSION_EXPIRED("session_expired"),
    SESSION_NOT_FOUND("session_not_found"),
    UNEXPECTED_FAILURE("unexpected_failure"),
    NETWORK_ERROR(null);

    companion object {
        fun fromSupabaseCode(code: String?): ErrorCode {
            val normalizedCode = code?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }
                ?: return UNEXPECTED_FAILURE

            return entries.firstOrNull { it.supabaseCode == normalizedCode } ?: UNEXPECTED_FAILURE
        }
    }
}
