package com.cuervolu.witcherscodex.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.data.network.AuthenticationService
import com.cuervolu.witcherscodex.ui.login.LoginActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.SmoothBottomBar
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(), LogoutListener {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, DashboardActivity::class.java)

    }

    @Inject
    lateinit var authService: AuthenticationService
    private lateinit var bottomBar: SmoothBottomBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Verificar la autenticación antes de continuar
        if (!authService.isUserAuthenticated()) {
            // El usuario no está autenticado, redirigirlo a la pantalla de inicio de sesión
            startActivity(LoginActivity.create(this))
            finish() // Cierra la actividad actual para evitar que el usuario vuelva atrás.
            return
        }
        bottomBar = findViewById(R.id.bottomBar)
        supportFragmentManager.commit(allowStateLoss = true) {
            setReorderingAllowed(true)
            add(R.id.fragment_container, HomeFragment())
        }
        bottomBar.itemActiveIndex = 2
        bottomMenu()
    }

    private fun bottomMenu() {
        Timber.d("Configurando el menú inferior")
        val navigationItems = listOf(
            0 to FactsFragment(),
            2 to HomeFragment(),
            4 to ProfileFragment()
        )


        bottomBar.setOnItemSelectedListener  { itemId ->
            Timber.d("Item seleccionado: $itemId")
            val fragmentClass = navigationItems.firstOrNull { it.first == itemId }?.second
            fragmentClass?.let { navigateToFragment(it) }
        }
    }

    /**
     * Reemplaza el fragmento actual con el fragmento seleccionado.
     */
    private fun navigateToFragment(fragmentClass: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        val containerId = currentFragment!!.id

        supportFragmentManager.beginTransaction()
            .replace(containerId, fragmentClass)
            .commit()

        Timber.d("Fragmento actual: ${fragmentClass::class.java.simpleName}")
    }

    override fun onLogout() {
        finish()
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

}