package com.example.askceny.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.askceny.domain.repositories.AuthRepository
import com.example.askceny.domain.types.AuthState
import com.example.askceny.domain.types.ErrorCode
import com.example.askceny.domain.validation.AuthValidationResult
import com.example.askceny.domain.validation.AuthValidator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState : MutableStateFlow<AuthState> = MutableStateFlow(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    private val _emailError : MutableStateFlow<String> = MutableStateFlow<String>("")
    val emailError: StateFlow<String> = _emailError

    private val _passwordError : MutableStateFlow<String> = MutableStateFlow<String>("")
    val passwordError: StateFlow<String> = _passwordError

    private val _displayNameError : MutableStateFlow<String> = MutableStateFlow<String>("")
    val displayNameError: StateFlow<String> = _displayNameError

    private val _otpError : MutableStateFlow<String> = MutableStateFlow("")
    val otpError: StateFlow<String> = _otpError

    private val _otpInfo : MutableStateFlow<String> = MutableStateFlow("")
    val otpInfo: StateFlow<String> = _otpInfo

    init {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val currentUser = try {
                authRepository.getCurrentUser()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                println("AUTH_BOOTSTRAP: current user lookup failed: ${e.message}")
                null
            }

            if (currentUser != null) {
                _authState.value = AuthState.Authenticated
            }else{
                _authState.value = AuthState.Unauthenticated
            }

            /* To test if start navigation function is working */
//            signIn("test@test.com","test123") /* for test only */
            /* ----------------------------------------------- */
        }
    }

    fun signUp(displayName: String, email: String, password: String) {
        val validation = AuthValidator.validateSignUp(displayName, email, password)
        applyValidationResult(validation)
        if (!validation.isValid) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val res = authRepository.createUserWithEmailAndPassword(
                displayName.trim(),
                email.trim(),
                password.trim()
            )
            _authState.value = res
            applyAuthError(res)
        }
    }

    fun signIn(email: String, password: String) {
        val validation = AuthValidator.validateSignIn(email, password)
        applyValidationResult(validation)
        if (!validation.isValid) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val res = authRepository.signInWithEmailAndPassword(email.trim(), password.trim())
            _authState.value = res
            applyAuthError(res)
        }
    }

    fun verifyEmailOtp(email: String, token: String) {
        val normalizedEmail = email.trim()
        val normalizedToken = token.trim()
        _otpInfo.value = ""

        if (normalizedToken.isBlank()) {
            _otpError.value = "Enter the verification code"
            return
        }

        _otpError.value = ""
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val res = authRepository.verifyEmailOtp(normalizedEmail, normalizedToken)
            _authState.value = res
            applyOtpResult(res)
        }
    }

    fun resendSignUpEmailOtp(email: String) {
        val normalizedEmail = email.trim()
        _otpError.value = ""
        _otpInfo.value = ""
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            val res = authRepository.resendSignUpEmailOtp(normalizedEmail)
            _authState.value = res
            applyOtpResult(res, showResendSuccess = true)
        }
    }

    fun dismissEmailConfirmationRequired() {
        if (_authState.value is AuthState.EmailConfirmationRequired) {
            _authState.value = AuthState.Unauthenticated
        }
        _otpError.value = ""
        _otpInfo.value = ""
    }

    private fun applyValidationResult(validation: AuthValidationResult) {
        _displayNameError.value = validation.displayNameError
        _emailError.value = validation.emailError
        _passwordError.value = validation.passwordError
    }

    private fun applyAuthError(authState: AuthState) {
        if (authState !is AuthState.AuthError) return

        when (authState.errorCode) {
            ErrorCode.INVALID_CREDENTIALS -> {
                _emailError.value = "Invalid email or password"
                _passwordError.value = "Invalid email or password"
            }
            ErrorCode.EMAIL_ADDRESS_INVALID,
            ErrorCode.VALIDATION_FAILED -> { _emailError.value = "Invalid email" }
            ErrorCode.EMAIL_EXISTS,
            ErrorCode.USER_ALREADY_EXISTS -> { _emailError.value = "Email already in use" }
            ErrorCode.WEAK_PASSWORD -> { _passwordError.value = "Weak password" }
            ErrorCode.OVER_REQUEST_RATE_LIMIT,
            ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT -> { _emailError.value = "Too many attempts. Try again later." }
            ErrorCode.REQUEST_TIMEOUT -> { _emailError.value = "Request timed out. Try again." }
            ErrorCode.UNEXPECTED_FAILURE -> { _emailError.value = "Something went wrong. Try again." }
            else -> { Unit }
        }
    }

    private fun applyOtpResult(authState: AuthState, showResendSuccess: Boolean = false) {
        when (authState) {
            is AuthState.AuthError -> {
                _otpError.value = when (authState.errorCode) {
                    ErrorCode.INVALID_CREDENTIALS,
                    ErrorCode.VALIDATION_FAILED,
                    ErrorCode.EMAIL_NOT_CONFIRMED,
                    ErrorCode.OTP_EXPIRED,
                    ErrorCode.SESSION_EXPIRED,
                    ErrorCode.SESSION_NOT_FOUND -> "Invalid or expired verification code"
                    ErrorCode.OVER_REQUEST_RATE_LIMIT,
                    ErrorCode.OVER_EMAIL_SEND_RATE_LIMIT -> "Too many attempts. Try again later."
                    ErrorCode.REQUEST_TIMEOUT -> "Request timed out. Try again."
                    else -> "Something went wrong. Try again."
                }
            }
            is AuthState.EmailConfirmationRequired -> {
                if (showResendSuccess) {
                    _otpInfo.value = "Verification code resent"
                }
            }
            AuthState.Authenticated -> {
                _otpError.value = ""
                _otpInfo.value = ""
            }
            else -> Unit
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun updateEmailError() {
        updateState()
        _emailError.value = ""
    }

    fun updatePasswordError() {
        updateState()
        _passwordError.value = ""
    }

    fun updateDisplayNameError() {
        updateState()
        _displayNameError.value = ""
    }

    fun updateOtpError() {
        updateState()
        _otpError.value = ""
        _otpInfo.value = ""
    }

    private fun updateState() {
        if (authState.value is AuthState.AuthError) {
            println("AUTH_LOGIN_COLUMN_UPDATE: ´´ UPDATE CALLED")
            _authState.value = AuthState.Unauthenticated
        }
    }


    companion object {
        val USER_REPOSITORY_KEY = object : CreationExtras.Key<AuthRepository> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val authRepository = this[USER_REPOSITORY_KEY] as AuthRepository
                AuthViewModel(
                    authRepository = authRepository
                )
            }
        }
    }
}
