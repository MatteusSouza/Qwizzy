package com.example.askceny.data.remote.api

import com.example.askceny.data.remote.SupabaseConfigHolder
import com.example.askceny.domain.types.ErrorCode
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import io.github.jan.supabase.createSupabaseClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

internal object SupabaseAuthSessionClientFactory {
    fun create(): SupabaseAuthSessionClient {
        val config = SupabaseConfigHolder.current()

        if (!config.isConfigured) {
            return UnavailableSupabaseAuthSessionClient
        }

        return runCatching {
            SupabaseKtAuthSessionClient(
                createSupabaseClient(
                    supabaseUrl = config.url.trim(),
                    supabaseKey = config.publishableKey.trim(),
                ) {
                    install(Auth)
                },
            )
        }.getOrElse {
            UnavailableSupabaseAuthSessionClient
        }
    }
}

private object UnavailableSupabaseAuthSessionClient : SupabaseAuthSessionClient {
    override val isPlaceholder: Boolean = false

    override fun currentUserOrNull(): SupabaseAuthUser? = null

    override fun signOut() {}

    override suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signUpWithEmail(
        displayName: String,
        email: String,
        password: String,
    ): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        throw SupabaseAuthFailureException(ErrorCode.UNEXPECTED_FAILURE.supabaseCode)
    }
}

internal class SupabaseKtAuthSessionClient(
    private val client: SupabaseClient,
) : SupabaseAuthSessionClient {
    override val isPlaceholder: Boolean = false

    override fun currentUserOrNull(): SupabaseAuthUser? {
        return client.auth.currentUserOrNull()?.toSupabaseAuthUser()
    }

    override fun signOut() {
        runBlocking {
            client.auth.signOut()
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): SupabaseAuthSuccess {
        return mapAuthFailure {
            client.auth.signInWith(Email, redirectUrl = null) {
                this.email = email
                this.password = password
            }

            SupabaseAuthSuccess.Authenticated(client.auth.currentUserOrNull()?.toSupabaseAuthUser())
        }
    }

    override suspend fun signUpWithEmail(
        displayName: String,
        email: String,
        password: String,
    ): SupabaseAuthSuccess {
        return mapAuthFailure {
            val user = client.auth.signUpWith(Email, redirectUrl = null) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("displayName", displayName)
                    put("username", displayName)
                }
            }

            if (user == null) {
                SupabaseAuthSuccess.Authenticated(client.auth.currentUserOrNull()?.toSupabaseAuthUser())
            } else {
                SupabaseAuthSuccess.EmailConfirmationRequired(user.toSupabaseAuthUser())
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        return signInWithGoogleIdToken(idToken, nonce)
    }

    override suspend fun signUpWithGoogle(idToken: String, nonce: String?): SupabaseAuthSuccess {
        return signInWithGoogleIdToken(idToken, nonce)
    }

    private suspend fun signInWithGoogleIdToken(idToken: String, nonce: String?): SupabaseAuthSuccess {
        return mapAuthFailure {
            client.auth.signInWith(IDToken, redirectUrl = null) {
                this.idToken = idToken
                provider = Google
                this.nonce = nonce
            }

            SupabaseAuthSuccess.Authenticated(client.auth.currentUserOrNull()?.toSupabaseAuthUser())
        }
    }

    private suspend fun mapAuthFailure(block: suspend () -> SupabaseAuthSuccess): SupabaseAuthSuccess {
        return try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: AuthWeakPasswordException) {
            throw SupabaseAuthFailureException(ErrorCode.WEAK_PASSWORD.supabaseCode, e)
        } catch (e: AuthRestException) {
            throw SupabaseAuthFailureException(e.errorCode?.value ?: e.error, e)
        } catch (e: HttpRequestTimeoutException) {
            throw SupabaseAuthFailureException(ErrorCode.REQUEST_TIMEOUT.supabaseCode, e)
        }
    }

    private fun UserInfo.toSupabaseAuthUser(): SupabaseAuthUser {
        return SupabaseAuthUser(
            id = id,
            email = email,
            displayName = userMetadata.stringValue("displayName")
                ?: userMetadata.stringValue("display_name")
                ?: userMetadata.stringValue("name"),
            username = userMetadata.stringValue("username"),
        )
    }

    private fun JsonObject?.stringValue(name: String): String? {
        return this?.get(name)?.jsonPrimitive?.contentOrNull
    }
}
