package com.example.askceny.data.local

import com.example.askceny.data.models.User

class MockedAuthManager() {

    companion object {
        private var token: String? = null

        fun getToken(): String? {
            return token
        }

        fun getUser(): User? {
            if (token != null) {
                return MockServerApi.getUser()
            }
            return null
        }
    }

    fun signUpWithEmail(email: String, displayName: String, username: String, password: String) : Boolean {
        token = MockServerApi.createUser(email, username, displayName, password)
        return token != null
    }

    fun login(email: String, password: String): Boolean {
        token = MockServerApi.login(email, password)
        return token != null
    }

    fun logoff(): Boolean {
        token?.let {
            MockServerApi.logoff(it)
            return true
        }
        return false
    }

    fun isAuthenticated(): Boolean {
        return MockServerApi.isAuthenticated()
    }

    fun getUser(): User? {
        return MockedAuthManager.getUser()
    }
}