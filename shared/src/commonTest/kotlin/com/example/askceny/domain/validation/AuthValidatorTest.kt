package com.example.askceny.domain.validation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AuthValidatorTest {
    @Test
    fun `sign-in rejects blank email`() {
        val result = AuthValidator.validateSignIn("", "password")

        assertFalse(result.isValid)
        assertEquals("Email can not be empty", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `sign-in rejects blank password`() {
        val result = AuthValidator.validateSignIn("user@example.com", "")

        assertFalse(result.isValid)
        assertEquals("", result.emailError)
        assertEquals("Password can not be empty", result.passwordError)
    }

    @Test
    fun `sign-in rejects malformed email`() {
        val result = AuthValidator.validateSignIn("not-an-email", "password")

        assertFalse(result.isValid)
        assertEquals("Invalid email", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `sign-in trims whitespace before validation`() {
        val result = AuthValidator.validateSignIn("  user@example.com  ", "  password  ")

        assertTrue(result.isValid)
        assertEquals("", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `sign-up rejects blank display name`() {
        val result = AuthValidator.validateSignUp("", "user@example.com", "password")

        assertFalse(result.isValid)
        assertEquals("Name can not be empty", result.displayNameError)
        assertEquals("", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `sign-up rejects blank email`() {
        val result = AuthValidator.validateSignUp("User", "", "password")

        assertFalse(result.isValid)
        assertEquals("", result.displayNameError)
        assertEquals("Email can not be empty", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `sign-up rejects blank password`() {
        val result = AuthValidator.validateSignUp("User", "user@example.com", "")

        assertFalse(result.isValid)
        assertEquals("", result.displayNameError)
        assertEquals("", result.emailError)
        assertEquals("Password can not be empty", result.passwordError)
    }

    @Test
    fun `sign-up rejects malformed email`() {
        val result = AuthValidator.validateSignUp("User", "not-an-email", "password")

        assertFalse(result.isValid)
        assertEquals("", result.displayNameError)
        assertEquals("Invalid email", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `valid sign-in input returns no field errors`() {
        val result = AuthValidator.validateSignIn("user@example.com", "password")

        assertTrue(result.isValid)
        assertEquals("", result.emailError)
        assertEquals("", result.passwordError)
    }

    @Test
    fun `valid sign-up input returns no field errors`() {
        val result = AuthValidator.validateSignUp("User", "user@example.com", "password")

        assertTrue(result.isValid)
        assertEquals("", result.displayNameError)
        assertEquals("", result.emailError)
        assertEquals("", result.passwordError)
    }
}
