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
            ErrorCode.UNEXPECTED_FAILURE -> { _emailError.value = "Something went wrong. Try again." }
            else -> { Unit }
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
