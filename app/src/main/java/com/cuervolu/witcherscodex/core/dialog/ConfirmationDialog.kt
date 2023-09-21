package com.cuervolu.witcherscodex.core.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.cuervolu.witcherscodex.databinding.DialogConfirmationBinding

interface ConfirmationDialogListener {
    fun onConfirmationResult(accepted: Boolean)
}


class ConfirmationDialog : DialogFragment() {
    private var dialogCallback: ((confirmed: Boolean) -> Unit)? = null
    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_MESSAGE = "message"

        fun create(title: String, message: String): ConfirmationDialog {
            val dialog = ConfirmationDialog()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }

    fun setDialogCallback(callback: (confirmed: Boolean) -> Unit) {
        dialogCallback = callback
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogConfirmationBinding.inflate(requireActivity().layoutInflater)
        val title = arguments?.getString(ARG_TITLE) ?: ""
        val message = arguments?.getString(ARG_MESSAGE) ?: ""

        binding.tvTitle.text = title
        binding.tvDescription.text = message

        val dialog = AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        // Manejo del botón "Aceptar"
        binding.btnPositive.setOnClickListener {
            // Envía true para indicar que el usuario aceptó
            sendResult(true)
            dismiss()
        }

        // Manejo del botón "Cancelar"
        binding.btnNegative.setOnClickListener {
            // Envía false para indicar que el usuario canceló
            sendResult(false)
            dismiss()
        }

        return dialog
    }

    private fun sendResult(result: Boolean) {
        dialogCallback?.invoke(result)
    }

}
