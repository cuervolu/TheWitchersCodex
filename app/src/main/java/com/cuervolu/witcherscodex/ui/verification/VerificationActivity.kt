package com.cuervolu.witcherscodex.ui.verification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
import com.cuervolu.witcherscodex.databinding.ActivityVerificationBinding
import com.cuervolu.witcherscodex.ui.dashboard.DashboardActivity
import com.cuervolu.witcherscodex.utils.MusicPlayerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Actividad de verificación que muestra la pantalla de verificación de cuenta.
 *
 * Esta actividad permite al usuario verificar su cuenta y navegar a la pantalla de detalles una vez que
 * la verificación se ha completado con éxito.
 *
 * @property dialogLauncher Un objeto utilizado para mostrar diálogos.
 * @property binding La instancia de [ActivityVerificationBinding] que representa la vista de esta actividad.
 * @property verificationViewModel El [VerificationViewModel] utilizado para gestionar la lógica de la actividad.
 */
@AndroidEntryPoint
class VerificationActivity : AppCompatActivity() {

    companion object {
        fun create(context: Context): Intent =
            Intent(context, VerificationActivity::class.java)
    }

    @Inject
    lateinit var dialogLauncher: DialogFragmentLauncher

    private lateinit var binding: ActivityVerificationBinding
    private val verificationViewModel: VerificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        initListeners()
        initObservers()
    }

    private fun initListeners() {
        binding.btnGoToDetail.setOnClickListener { verificationViewModel.onGoToDetailSelected() }
    }

    private fun initObservers() {
        verificationViewModel.navigateToVerifyAccount.observe(this) {
            it.getContentIfNotHandled()?.let {
                goToDashboard()
            }
        }

        verificationViewModel.showContinueButton.observe(this) {
            it.getContentIfNotHandled()?.let {
                binding.btnGoToDetail.isVisible = true
            }
        }
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

    private fun goToDashboard() {
        startActivity(DashboardActivity.create(this))
    }
}