package com.example.askceny.data.remote

data class SupabaseConfig(
    val url: String = "",
    val publishableKey: String = "",
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() && publishableKey.isNotBlank()
}

object SupabaseConfigHolder {
    private var config: SupabaseConfig = SupabaseConfig()

    fun initialize(config: SupabaseConfig) {
        this.config = config
    }

    fun current(): SupabaseConfig {
        return config
    }
}
