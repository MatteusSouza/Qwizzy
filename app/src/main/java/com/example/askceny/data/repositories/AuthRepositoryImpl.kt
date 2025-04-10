package com.example.askceny.data.repositories

/*
class AuthRepositoryImpl() : AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun createUserWithEmailAndPassword(email: String, password: String) : Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(AuthException(mapFirebaseError(e)))
        } catch (e: Exception) {
            Result.failure(AuthException(ErrorCode.UNKNOWN_ERROR))
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) : Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.failure(AuthException(mapFirebaseError(e)))
        } catch (e: Exception) {
            Result.failure(AuthException(ErrorCode.UNKNOWN_ERROR))
        }
    }

    override fun getCurrentUser(): User? {
        val res = auth.currentUser
        if(res == null)
            return null

        val user = User(id=res.uid, displayName = res.displayName ?: "", username = "", email = res.email ?: "" ,about = "", website = "")
        return user
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun isEmail(email: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun emailExists(email: String): Boolean {
        TODO("Not yet implemented")
    }

    private fun mapFirebaseError(exception: FirebaseAuthException): ErrorCode {
        return when (exception.errorCode) {
            "ERROR_INVALID_EMAIL" -> ErrorCode.INVALID_EMAIL
            "ERROR_WRONG_PASSWORD" -> ErrorCode.WRONG_PASSWORD
            "ERROR_USER_DISABLED" -> ErrorCode.USER_DISABLED
            "ERROR_USER_NOT_FOUND" -> ErrorCode.USER_NOT_FOUND
            "ERROR_EMAIL_ALREADY_IN_USE" -> ErrorCode.EMAIL_ALREADY_IN_USE
            "ERROR_WEAK_PASSWORD" -> ErrorCode.WEAK_PASSWORD
            "ERROR_NETWORK_REQUEST_FAILED" -> ErrorCode.NETWORK_ERROR
            "ERROR_TOO_MANY_REQUESTS" -> ErrorCode.ERROR_TOO_MANY_REQUESTS
            "UNAUTHENTICATED" -> ErrorCode.UNAUTHENTICATED
            else -> ErrorCode.UNKNOWN_ERROR
        }
    }
}


 */