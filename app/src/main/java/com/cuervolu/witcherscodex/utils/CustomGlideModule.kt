package com.cuervolu.witcherscodex.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.cuervolu.witcherscodex.R
import com.cuervolu.witcherscodex.core.dialog.ErrorDialog

/**
 * Esta clase, [GlideImageLoader], proporciona funciones para cargar imágenes de forma eficiente
 * utilizando la biblioteca Glide. Además, maneja errores de carga de imágenes y muestra un
 * [ErrorDialog] personalizado en caso de fallo en la carga de la imagen.
 */
object GlideImageLoader {

    /**
     * Carga una imagen en un [ImageView] utilizando Glide y maneja errores de carga de imagen.
     *
     * @param imageView La vista [ImageView] en la que se debe mostrar la imagen.
     * @param imageUrl La URL de la imagen que se va a cargar.
     */
    fun loadImage(imageView: ImageView, imageUrl: String) {
        // Configura las opciones de carga de imagen (si es necesario)
        val options = RequestOptions()
            .error(R.drawable.image_placeholder_error) // Establece el recurso de error personalizado

        // Carga la imagen utilizando Glide
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(options)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    // Muestra el diálogo de error cuando la carga de la imagen falla
                    showErrorDialog(imageView.context, e?.localizedMessage ?: "Error desconocido")
                    return false // Retorna false para que Glide no maneje el error
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    // Actualiza la vista con la imagen cargada
                    imageView.setImageDrawable(resource)
                    return true // Retorna true para indicar que Glide ha manejado la carga de la imagen
                }
            })
            .into(imageView)
    }

    /**
     * Muestra un [ErrorDialog] personalizado en caso de error de carga de imagen.
     *
     * @param context El contexto de la aplicación.
     * @param errorMessage El mensaje de error que se mostrará en el diálogo.
     */
    private fun showErrorDialog(context: Context, errorMessage: String) {
        // Crea y muestra el diálogo de error utilizando tu fragmento de diálogo personalizado
        val errorDialog = ErrorDialog.create(
            title = "Error de carga",
            description = errorMessage
        )
        (context as? AppCompatActivity)?.supportFragmentManager?.let { errorDialog.show(it, "ErrorDialog") }
    }

}

/**
 * Esta clase, [CustomGlideModule], es una extensión de [AppGlideModule] que personaliza la configuración
 * de Glide para la carga de imágenes. Esto incluye la configuración de la caché, el formato de compresión,
 * el tiempo de vida de la caché y otras opciones relacionadas con la carga de imágenes.
 */
@GlideModule
class CustomGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // Configura el tamaño máximo de la caché a 100 MB
        builder.setMemoryCache(LruResourceCache(100 * 1024 * 1024))
        // Configura el formato de compresión predeterminado a JPEG con calidad 80%
        builder.setDefaultRequestOptions(RequestOptions.formatOf(DecodeFormat.PREFER_RGB_565))
        // Configura el tiempo de vida de la caché a 2 días
        builder.setDiskCache(DiskLruCacheFactory(context.cacheDir.path, "glide", 2 * 1024 * 1024))
        // Configura opciones de red, por ejemplo, un tiempo de espera de 10 segundos
        builder.setDefaultRequestOptions(RequestOptions().timeout(10000))
    }
}
