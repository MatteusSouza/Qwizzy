package com.example.askceny

import android.app.Application
import com.example.askceny.data.di.initKoin
import com.example.askceny.data.local.setDatabaseBuilderContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class AskCenyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setDatabaseBuilderContext(this)

        initKoin {
            androidLogger()
            androidContext(this@AskCenyApplication)
        }
    }
}
