package com.example.askceny.data.repositories

import com.example.askceny.data.models.User
import com.example.askceny.data.types.AuthState
import com.example.askceny.data.types.ErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await


class AuthRepositoryImpl() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun createUserWithEmailAndPassword(email: String, password: String) : AuthState {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            println("AuthRepositoryImpl: CreateUserWithEmailAndPassword: ${auth.currentUser}")
            AuthState.Authenticated
        } catch (e: FirebaseAuthException) {
            println("AuthRepositoryImpl: CreateUserWithEmailAndPassword: ${e.errorCode}")
            AuthState.AuthError(mapFirebaseError(e))
        } catch (e: Exception) {
            println("AuthRepositoryImpl: CreateUserWithEmailAndPassword: $e")
            AuthState.AuthError(ErrorCode.UNKNOWN_ERROR)
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) : AuthState {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            println("AuthRepositoryImpl: SignInWithEmailAndPassword: ${auth.currentUser}")
            AuthState.Authenticated
        } catch (e: FirebaseAuthException) {
            println("AuthRepositoryImpl: SignInWithEmailAndPassword: ${e.errorCode}")
            AuthState.AuthError(mapFirebaseError(e))
        } catch (e: Exception) {
            println("AuthRepositoryImpl: SignInWithEmailAndPassword: $e")
            AuthState.AuthError(ErrorCode.UNKNOWN_ERROR)
        }
    }

    override fun getCurrentUser(): User? {
        auth.currentUser?.reload()
        val res = auth.currentUser
        println("AuthRepositoryImpl getCurrentUser: ${res?.uid}")
        if(res == null)
            return null
        return User(id=res.uid, displayName = res.displayName ?: "", username = "", email = res.email ?: "" ,about = "", website = "")
    }

    override fun signOut() {
        auth.signOut()
    }

    private fun mapFirebaseError(exception: FirebaseAuthException): ErrorCode {
        println("AuthRepositoryImpl: mapFirebaseError message: ${exception.message}")
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> ErrorCode.INVALID_EMAIL // When email is badly formated
            "ERROR_INVALID_CREDENTIAL" -> ErrorCode.ERROR_INVALID_CREDENTIAL // when email or password is wrong
            "ERROR_USER_DISABLED" -> ErrorCode.USER_DISABLED
            "ERROR_EMAIL_ALREADY_IN_USE" -> ErrorCode.EMAIL_ALREADY_IN_USE
            "ERROR_WEAK_PASSWORD" -> ErrorCode.WEAK_PASSWORD
            "ERROR_NETWORK_REQUEST_FAILED" -> ErrorCode.NETWORK_ERROR
            "ERROR_TOO_MANY_REQUESTS" -> ErrorCode.ERROR_TOO_MANY_REQUESTS
            else -> ErrorCode.UNKNOWN_ERROR
        }
    }
}