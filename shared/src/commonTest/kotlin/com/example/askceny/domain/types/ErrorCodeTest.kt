package com.example.askceny.domain.types

import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorCodeTest {
    @Test
    fun `fromSupabaseCode maps direct Supabase auth codes`() {
        assertEquals(ErrorCode.OTP_EXPIRED, ErrorCode.fromSupabaseCode("otp_expired"))
        assertEquals(ErrorCode.OTP_DISABLED, ErrorCode.fromSupabaseCode("otp_disabled"))
        assertEquals(
            ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT,
            ErrorCode.fromSupabaseCode("over_email_send_rate_limit")
        )
    }

    @Test
    fun `fromSupabaseCode maps known codes embedded in exception messages`() {
        assertEquals(
            ErrorCode.OTP_EXPIRED,
            ErrorCode.fromSupabaseCode("AuthRestException: otp_expired: token has expired")
        )
        assertEquals(
            ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT,
            ErrorCode.fromSupabaseCode("AuthRestException: over_email_send_rate_limit")
        )
    }

    @Test
    fun `fromSupabaseCode maps known Supabase auth messages`() {
        assertEquals(
            ErrorCode.USER_ALREADY_EXISTS,
            ErrorCode.fromSupabaseCode("User already registered")
        )
        assertEquals(
            ErrorCode.INVALID_CREDENTIALS,
            ErrorCode.fromSupabaseCode("Invalid login credentials")
        )
        assertEquals(
            ErrorCode.EMAIL_NOT_CONFIRMED,
            ErrorCode.fromSupabaseCode("Email not confirmed")
        )
    }

    @Test
    fun `fromSupabaseCode keeps unknown errors generic`() {
        assertEquals(ErrorCode.UNEXPECTED_FAILURE, ErrorCode.fromSupabaseCode("unmapped error"))
    }
}
