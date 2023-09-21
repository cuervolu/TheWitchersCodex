package com.cuervolu.witcherscodex.core.dialog

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cuervolu.witcherscodex.core.delegate.weak
import javax.inject.Inject

/**
 * Clase encargada de mostrar [DialogFragment] en una [FragmentActivity]. Esta clase se integra con el ciclo de vida
 * de la actividad para asegurarse de que el [DialogFragment] se muestre correctamente cuando la actividad esté en
 * estado [RESUMED].
 *
 * @constructor Crea una instancia de [DialogFragmentLauncher].
 */
class DialogFragmentLauncher @Inject constructor() : LifecycleObserver {

    /**
     * Referencia débil a la [FragmentActivity] donde se mostrará el [DialogFragment].
     */
    private var activity: FragmentActivity? by weak()

    /**
     * Referencia débil al [DialogFragment] que se mostrará.
     */
    private var dialogFragment: DialogFragment? by weak()

    /**
     * Muestra el [DialogFragment] en la [FragmentActivity] especificada. Si la actividad ya está en estado [RESUMED],
     * el [DialogFragment] se muestra de inmediato. De lo contrario, se almacenan las referencias a la actividad y al
     * [DialogFragment], y se agrega esta instancia como observador del ciclo de vida de la actividad.
     *
     * @param dialogFragment El [DialogFragment] que se mostrará.
     * @param activity La [FragmentActivity] donde se mostrará el [DialogFragment].
     */
    fun show(dialogFragment: DialogFragment, activity: FragmentActivity) {
        if (activity.lifecycle.currentState.isAtLeast(RESUMED)) {
            dialogFragment.show(activity.supportFragmentManager, null)
        } else {
            this.activity = activity
            this.dialogFragment = dialogFragment
            activity.lifecycle.addObserver(this@DialogFragmentLauncher)
        }
    }

    /**
     * Callback que se llama cuando la [FragmentActivity] entra en estado [RESUMED]. En este punto, se muestra el
     * [DialogFragment] almacenado y se elimina esta instancia como observador del ciclo de vida de la actividad.
     *
     * @param owner El [LifecycleOwner] cuyo ciclo de vida ha alcanzado el estado [RESUMED].
     */
    fun onActivityResumed(owner: LifecycleOwner) {
        val activity = activity ?: return
        val dialogFragment = dialogFragment ?: return

        dialogFragment.show(activity.supportFragmentManager, null)
        activity.lifecycle.removeObserver(this)
    }
}