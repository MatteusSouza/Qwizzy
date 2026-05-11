package com.example.askceny

import androidx.compose.ui.window.ComposeUIViewController
import com.example.askceny.data.di.initKoin
import com.example.askceny.data.remote.SupabaseConfig
import com.example.askceny.data.remote.SupabaseConfigHolder
import com.example.askceny.presentation.di.presentationModule
import platform.Foundation.NSBundle

private var isKoinStarted = false

class KoinHelper {
    fun start() {
        initializeSupabaseConfig()
        if (!isKoinStarted) {
            initKoin {
                modules(presentationModule)
            }
            isKoinStarted = true
        }
    }
}

private fun initializeSupabaseConfig() {
    SupabaseConfigHolder.initialize(
        SupabaseConfig(
            url = infoValue("SUPABASE_URL"),
            publishableKey = infoValue("SUPABASE_PUBLISHABLE_KEY"),
        ),
    )
}

private fun infoValue(name: String): String {
    return NSBundle.mainBundle.objectForInfoDictionaryKey(name) as? String ?: ""
}

fun MainViewController() = ComposeUIViewController {
    App()
}
