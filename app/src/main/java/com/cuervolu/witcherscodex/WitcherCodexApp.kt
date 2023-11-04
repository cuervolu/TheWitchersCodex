package com.cuervolu.witcherscodex

import android.app.Application
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * Clase de aplicación principal de WitcherCodex.
 *
 * Esta clase se utiliza como punto de entrada de la aplicación y se encarga de la inicialización
 * de componentes importantes, como Dagger-Hilt y Timber para el registro de logs.
 *
 * @property BuildConfig.DEBUG Indica si la aplicación se está ejecutando en modo de depuración.
 */
@HiltAndroidApp
class WitcherCodexApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeTimber()
        MusicPlayerManager.initialize(this)
        MusicPlayerManager.startPlaying(this)

        // Inicializa FirebaseApp
        FirebaseApp.initializeApp(this)

        // Configura el proveedor de App Check (Play Integrity)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }

    private fun initializeTimber() {

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}
