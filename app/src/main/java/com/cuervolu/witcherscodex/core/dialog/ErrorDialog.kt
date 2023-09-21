package com.cuervolu.witcherscodex.core.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.cuervolu.witcherscodex.databinding.DialogErrorBinding

/**
 * Clase [DialogFragment] que muestra un cuadro de diálogo de error personalizado. Permite configurar el título,
 * la descripción, la capacidad de cancelar el diálogo, así como las acciones positivas y negativas.
 */
class ErrorDialog : DialogFragment() {

    private var title: String = ""
    private var description: String = ""
    private var isDialogCancelable: Boolean = true
    private var positiveAction: Action = Action.Empty
    private var negativeAction: Action = Action.Empty

    companion object {
        /**
         * Crea una instancia de [ErrorDialog] con las opciones de configuración proporcionadas.
         *
         * @param title El título del cuadro de diálogo.
         * @param description La descripción del cuadro de diálogo.
         * @param isDialogCancelable Indica si el cuadro de diálogo es cancelable por el usuario.
         * @param positiveAction La acción positiva que se ejecutará al hacer clic en el botón positivo.
         * @param negativeAction La acción negativa que se ejecutará al hacer clic en el botón negativo.
         */
        fun create(
            title: String = "",
            description: String = "",
            isDialogCancelable: Boolean = true,
            positiveAction: Action = Action.Empty,
            negativeAction: Action = Action.Empty,
        ): ErrorDialog = ErrorDialog().apply {
            this.title = title
            this.description = description
            this.isDialogCancelable = isDialogCancelable
            this.positiveAction = positiveAction
            this.negativeAction = negativeAction
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window ?: return

        window.setLayout(MATCH_PARENT, WRAP_CONTENT)
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogErrorBinding.inflate(requireActivity().layoutInflater)

        binding.tvTitle.text = title
        binding.tvDescription.text = description
        if (negativeAction == Action.Empty) {
            binding.btnNegative.isGone = true
        } else {
            binding.btnNegative.text = negativeAction.text
            binding.btnNegative.setOnClickListener { negativeAction.onClickListener(this) }
        }
        binding.btnPositive.text = positiveAction.text
        binding.btnPositive.setOnClickListener { positiveAction.onClickListener(this) }
        isCancelable = isDialogCancelable

        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .setCancelable(isDialogCancelable)
            .create()
    }

    data class Action(
        val text: String,
        val onClickListener: (ErrorDialog) -> Unit
    ) {
        companion object {
            val Empty = Action("") {}
        }
    }
}
