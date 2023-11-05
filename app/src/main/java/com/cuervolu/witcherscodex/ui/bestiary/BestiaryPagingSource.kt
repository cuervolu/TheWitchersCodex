package com.cuervolu.witcherscodex.ui.bestiary

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class BestiaryPagingSource(
    private val queryMonstersByName: Query,
    private val isLoading: MutableStateFlow<Boolean>
) : PagingSource<QuerySnapshot, Bestiary>() {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, Bestiary>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Bestiary> {
        return try {
            isLoading.value = true
            val currentPage = params.key ?: queryMonstersByName.get().await()
            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryMonstersByName.startAfter(lastVisibleProduct).get().await()
            isLoading.value = false
            LoadResult.Page(
                data = currentPage.toObjects(Bestiary::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: FirebaseFirestoreException) {
            LoadResult.Error(e)
        }
    }
}
