package com.cuervolu.witcherscodex.core.ex

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cuervolu.witcherscodex.R


/**
 * Extensión para mostrar un [Toast] en una [Activity].
 *
 * @param text El texto que se mostrará en el [Toast].
 * @param length La duración del [Toast] (por defecto, [Toast.LENGTH_SHORT]).
 */
fun Activity.toast(text: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, length).show()
}

/**
 * Extensión para crear un [SpannableStringBuilder] que combina dos partes de texto con diferentes estilos.
 *
 * @param unselectedPart La parte del texto sin estilo.
 * @param selectedPart La parte del texto con estilo.
 * @return Un [SpannableStringBuilder] que combina ambas partes de texto con estilos diferentes.
 */
fun Activity.span(
    unselectedPart: String,
    selectedPart: String
): SpannableStringBuilder {
    val context: Context = this
    val completedText = SpannableStringBuilder("$unselectedPart $selectedPart")

    completedText.apply {
        setSpan(
            StyleSpan(Typeface.BOLD),
            unselectedPart.length,
            (unselectedPart + selectedPart).length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    completedText.apply {
        setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.secondary)),
            unselectedPart.length,
            (unselectedPart + selectedPart).length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return completedText
}