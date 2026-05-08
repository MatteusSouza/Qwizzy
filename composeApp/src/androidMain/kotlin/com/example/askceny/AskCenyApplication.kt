package com.example.askceny

import android.app.Application
import com.example.askceny.data.di.initKoin
import com.example.askceny.data.local.setDatabaseBuilderContext
import com.example.askceny.data.remote.SupabaseConfig
import com.example.askceny.data.remote.SupabaseConfigHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AskCenyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SupabaseConfigHolder.initialize(
            SupabaseConfig(
                url = getString(R.string.supabase_url),
                publishableKey = getString(R.string.supabase_publishable_key),
            ),
        )
        setDatabaseBuilderContext(this)

        initKoin {
            androidLogger()
            androidContext(this@AskCenyApplication)
        }
    }
}
