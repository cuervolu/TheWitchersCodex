package com.cuervolu.witcherscodex

import android.app.Application
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import dagger.hilt.android.HiltAndroidApp
import io.grpc.android.BuildConfig
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
    }

    private fun initializeTimber() {
        Timber.plant(DebugTree())
    }

}
