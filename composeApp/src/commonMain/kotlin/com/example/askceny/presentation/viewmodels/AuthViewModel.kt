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

            val currentUser = authRepository.getCurrentUser()
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
        _authState.value = AuthState.Loading

        val displayName = displayName.trim()
        val email = email.trim()
        val password = password.trim()
        if (displayName.isEmpty()) {_displayNameError.value = "Name can not be empty"}
        if (email.isEmpty()) {_emailError.value = "Email can not be empty"}
        if (password.isEmpty()) {_passwordError.value = "Password can not be empty"}
        if(email.isEmpty() || password.isEmpty()) { return }

        viewModelScope.launch {
            _authState.value = authRepository.createUserWithEmailAndPassword(displayName, email, password)
        }
    }

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading

        val email = email.trim()
        val password = password.trim()

        if (email.isEmpty()) {_emailError.value = "Email can not be empty"}
        if (password.isEmpty()) {_passwordError.value = "Password can not be empty"}
        if(email.isEmpty() || password.isEmpty()) { return }

        viewModelScope.launch {
            val res = authRepository.signInWithEmailAndPassword(email, password)
            _authState.value = res
            if (res is AuthState.AuthError) {
                when(res.errorCode) {
                    ErrorCode.ERROR_INVALID_CREDENTIAL -> {
                        _emailError.value = " "
                        _passwordError.value = " "
                    }
                    ErrorCode.INVALID_EMAIL -> { _emailError.value = "Invalid email" }
                    ErrorCode.EMAIL_ALREADY_IN_USE -> { _emailError.value = "Email already in use" }
                    ErrorCode.WEAK_PASSWORD -> { _passwordError.value = "Weak password" }
                    else -> { Unit }
                }
            }
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