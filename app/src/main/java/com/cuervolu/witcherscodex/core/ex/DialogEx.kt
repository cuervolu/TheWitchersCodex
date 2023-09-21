package com.cuervolu.witcherscodex.core.ex

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.cuervolu.witcherscodex.core.dialog.DialogFragmentLauncher
/**
 * Extensión para mostrar un [DialogFragment] utilizando un [DialogFragmentLauncher] en una [FragmentActivity].
 * Esta extensión simplifica la forma de mostrar fragmentos de diálogo en una actividad.
 *
 * @param launcher El [DialogFragmentLauncher] utilizado para mostrar el fragmento de diálogo.
 * @param activity La [FragmentActivity] en la que se mostrará el fragmento de diálogo.
 */
fun DialogFragment.show(launcher: DialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}