package com.cuervolu.witcherscodex.data.network

import com.cuervolu.witcherscodex.domain.models.Article
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleService @Inject constructor(
    private val firebase: FirebaseClient
) {
    // Método para crear un nuevo artículo
    fun createArticle(article: Article) {
        // Aquí puedes implementar la lógica para agregar el artículo a la fuente de datos
    }

    fun getArticles(onSuccess: (List<Article>) -> Unit, onError: () -> Unit) {
        // Accede a la colección "articles" en Firebase Firestore
        firebase.db.collection("articles")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val articles = mutableListOf<Article>()
                for (document in querySnapshot) {
                    // Convierte los documentos de Firestore en objetos de tipo Article
                    val article = document.toObject(Article::class.java)
                    articles.add(article)
                }
                // Llama a la función onSuccess con la lista de artículos
                onSuccess(articles)
            }
            .addOnFailureListener { exception ->
                Timber.e("Ha ocurrido un error al cargar los artículos: ${exception}")
                // Llama a la función onError para mostrar el diálogo de error
                onError()
            }
    }



    // Método para leer un artículo por su ID
    fun readArticle(articleId: String): Article? {
        // Aquí puedes implementar la lógica para obtener un artículo por su ID
        return null // Por defecto, se retorna null si el artículo no se encuentra
    }

    // Método para actualizar un artículo existente
    fun updateArticle(article: Article) {
        // Aquí puedes implementar la lógica para actualizar un artículo existente
    }

    // Método para eliminar un artículo por su ID
    fun deleteArticle(articleId: String) {
        // Aquí puedes implementar la lógica para eliminar un artículo por su ID
    }
}
