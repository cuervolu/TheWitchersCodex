package com.cuervolu.witcherscodex.ui.dashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.data.network.AuthenticationService
import com.cuervolu.witcherscodex.ui.login.LoginActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity(),  LogoutListener {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, DashboardActivity::class.java)

    }
    @Inject
    lateinit var authService: AuthenticationService

    private lateinit var chipNavigationBar: ChipNavigationBar
    private lateinit var fragment: Fragment

    // ViewModels asociados a los fragmentos
    private val homeViewModel: HomeViewModel by viewModels()
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val userProfileViewModel: ProfileViewModel by viewModels()

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

        // Inicializa la barra de navegación
        chipNavigationBar = findViewById(R.id.bottom_nav_menu)

        // Establece el elemento seleccionado por defecto
        chipNavigationBar.setItemSelected(R.id.navigation_dashboard, isSelected = true)
        // Reemplaza el contenedor de fragmentos con el fragmento de Dashboard
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment()).commit()

        // Configura la barra de navegación inferior
        bottomMenu()
    }

    /**
     * Configura la barra de navegación inferior y maneja la selección de elementos.
     */
    private fun bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener { itemId ->
            when (itemId) {
                R.id.navigation_dashboard -> {
                    // Observa LiveData en el ViewModel de Dashboard
                    homeViewModel.navigateToHome.observe(this, Observer {
                        // Maneja la navegación a través del ViewModel
                        navigateToFragment(HomeFragment())
                    })
                    // Dispara el evento en el ViewModel para la navegación
                    homeViewModel.onHomeSelected()
                }

                R.id.navigation_gallery -> {
                    galleryViewModel.navigateToGallery.observe(this, Observer {
                        navigateToFragment(GalleryFragment())
                    })
                    galleryViewModel.onGallerySelected()
                }

                R.id.navigation_profile -> {
                    userProfileViewModel.navigateToProfile.observe(this, Observer {
                        navigateToFragment(ProfileFragment())
                    })
                    userProfileViewModel.onProfileSelected()
                }
            }
        }
    }

    /**
     * Reemplaza el fragmento actual con el fragmento seleccionado.
     */
    private fun navigateToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
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