package org.jellyfin.mobile

import android.app.Application
import android.webkit.WebView
import coil.Coil
import org.jellyfin.mobile.controller.controllerModule
import org.jellyfin.mobile.model.databaseModule
import org.jellyfin.mobile.ui.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class JellyfinApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            // Setup logging
            Timber.plant(Timber.DebugTree())

            // Enable WebView debugging
            WebView.setWebContentsDebuggingEnabled(true)
        }

        startKoin {
            androidContext(this@JellyfinApplication)
            fragmentFactory()
            modules(applicationModule, controllerModule, databaseModule, uiModule)

            // Set Coil ImageLoader factory
            Coil.setImageLoader {
                koin.get()
            }
        }
    }
}
