package com.cuervolu.witcherscodex.core.ex

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Extensión para [EditText] que establece un [TextWatcher] que escucha los cambios de texto
 * en el [EditText] y ejecuta la función [listener] cuando se realiza un cambio.
 *
 * @param listener La función lambda que se ejecuta cuando cambia el texto en el [EditText].
 */
fun EditText.onTextChanged(listener: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

/**
 * Extensión para [EditText] que permite que el [EditText] pierda el foco después de realizar una acción específica,
 * como presionar una tecla de acción en el teclado virtual.
 *
 * @param action La acción específica (por ejemplo, [EditorInfo.IME_ACTION_DONE]) que activará la pérdida de foco.
 */
fun EditText.loseFocusAfterAction(action: Int) {
    this.setOnEditorActionListener { v, actionId, _ ->
        if (actionId == action) {
            this.dismissKeyboard()
            v.clearFocus()
        }
        return@setOnEditorActionListener false
    }
}