package com.cuervolu.witcherscodex.core.ex

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Extensión para [View] que permite ocultar el teclado virtual cuando se llama a esta función.
 *
 * @param completed Una función lambda opcional que se ejecutará después de intentar ocultar el teclado.
 * Esto puede ser útil para realizar acciones adicionales una vez que se ha ocultado el teclado.
 */
fun View.dismissKeyboard(completed: () -> Unit = {}) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val wasOpened = inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    if (!wasOpened) completed()
}