package com.cuervolu.witcherscodex.core.dialog


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.cuervolu.witcherscodex.databinding.DialogLoginSuccessBinding


/**
 * Clase [DialogFragment] que muestra un cuadro de diálogo de éxito de inicio de sesión. Este cuadro de diálogo se utiliza
 * para informar al usuario que el inicio de sesión se ha realizado con éxito.
 */
class LoginSuccessDialog : DialogFragment() {

    companion object {
        /**
         * Crea una instancia de [LoginSuccessDialog].
         *
         * @return Una instancia de [LoginSuccessDialog].
         */
        fun create(): LoginSuccessDialog = LoginSuccessDialog()
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogLoginSuccessBinding.inflate(requireActivity().layoutInflater)
        binding.btnPositive.setOnClickListener { dismiss() }

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(true)
            .create()
    }
}
