package com.example.askceny.data.repositories

import android.util.Log
import com.example.askceny.data.models.User
import com.example.askceny.data.types.AuthState
import com.example.askceny.data.types.ErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import kotlin.random.Random


class AuthRepositoryImpl() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override suspend fun createUserWithEmailAndPassword(displayName: String, email: String, password: String) : AuthState {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
            Log.d("AUTH_CREATE_USER", "uid: $uid")
            if (uid != null){
                val newUser = User(uid, displayName, "${displayName}${Random.nextInt(1000,9999)}", "","","")

                Log.d("AUTH_CREATE_USER", "createUserWithEmailAndPassword uid: $uid")
                db.collection("users")
                    .document(uid)
                    .set(newUser)
                    .await()
                Log.d("AUTH_CREATE_USER", "createUserWithEmailAndPassword Success to createUser ${newUser.id}")
            }
            AuthState.Authenticated
        } catch (e: FirebaseAuthException) {
            Log.e("AUTH_CREATE_USER", "FirebaseAuthException on CreateUserWithEmailAndPassword:\n ${e.errorCode}")
            AuthState.AuthError(mapFirebaseError(e))
        } catch (e: FirebaseFirestoreException) {
            Log.e("AUTH_CREATE_USER", "FirebaseAuthException on CreateUserWithEmailAndPassword:\n ${e.message}")
            AuthState.AuthError(ErrorCode.UNKNOWN_ERROR) // TODO: Implementing a map filter to handle FirebaseFirestoreException
        } catch (e: Exception) {
            Log.e("AUTH_CREATE_USER", "Unknown Exception on CreateUserWithEmailAndPassword:\n $e")
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