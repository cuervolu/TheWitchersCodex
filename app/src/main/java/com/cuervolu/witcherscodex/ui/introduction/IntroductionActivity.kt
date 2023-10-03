package com.cuervolu.witcherscodex.ui.introduction

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.databinding.ActivityIntroductionBinding
import com.cuervolu.witcherscodex.ui.dashboard.DashboardActivity
import com.cuervolu.witcherscodex.ui.login.LoginActivity
import com.cuervolu.witcherscodex.ui.signin.SignInActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * Clase de actividad principal de introducción.
 *
 * Esta actividad representa la pantalla de introducción de la aplicación. Proporciona opciones para que el usuario inicie sesión
 * o cree una cuenta nueva. Se conecta con [IntroductionViewModel] para gestionar la lógica de la interfaz de usuario y
 * la navegación.
 */
@AndroidEntryPoint
class IntroductionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroductionBinding
    private val introductionViewModel: IntroductionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        setTheme(R.style.Theme_Witcher)
        super.onCreate(savedInstanceState)
        binding = ActivityIntroductionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
    }


    override fun onResume() {
        super.onResume()

        // Reanuda la reproducción de música
        MusicPlayerManager.resumePlaying()
    }

    override fun onPause() {
        super.onPause()
        // Pausa la reproducción de música
        MusicPlayerManager.pausePlaying()
    }

    /**
     * Inicializa la interfaz de usuario y configura los observadores.
     */
    private fun initUI() {
        initListeners()
        initObservers()
    }

    /**
     * Inicializa los listeners de los botones.
     */
    private fun initListeners() {
        with(binding) {
            btnLogin.setOnClickListener { introductionViewModel.onLoginSelected() }
            btnSingIn.setOnClickListener { introductionViewModel.onSignInSelected() }
        }
    }

    /**
     * Inicializa los observadores para la navegación.
     */
    private fun initObservers() {
        introductionViewModel.navigateToLogin.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToLogin()
            }
        })
        introductionViewModel.navigateToSignIn.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                goToSignIn()
            }
        })

        introductionViewModel.navigateToDashboard.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToDashboard()
            }
        }
    }

    /**
     * Navega a la pantalla de inicio de sesión.
     */
    private fun goToSignIn() {
        startActivity(SignInActivity.create(this))
    }

    /**
     * Navega a la pantalla de registro.
     */
    private fun goToLogin() {
        startActivity(LoginActivity.create(this))
    }

    private fun goToDashboard() {
        startActivity(DashboardActivity.create(this))
        finish()
    }
}
