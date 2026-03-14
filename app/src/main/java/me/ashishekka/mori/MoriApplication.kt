package me.ashishekka.mori

import android.app.Application
import me.ashishekka.mori.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MoriApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MoriApplication)
            modules(appModule)
        }
    }
}
