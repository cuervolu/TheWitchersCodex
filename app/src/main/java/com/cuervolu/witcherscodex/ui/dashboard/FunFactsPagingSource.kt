package com.cuervolu.witcherscodex.ui.dashboard

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class FunFactsPagingSource(private val queryFactByName: Query, private val isLoading: MutableStateFlow<Boolean>): PagingSource<QuerySnapshot, FunFact>()  {

    override fun getRefreshKey(state: PagingState<QuerySnapshot, FunFact>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FunFact> {
        return try {
            isLoading.value = true
            val currentPage = params.key ?: queryFactByName.get().await()

            if (currentPage.isEmpty) {
                isLoading.value = false
                return LoadResult.Error(Exception("La lista está vacía"))
            }


            val lastVisibleProduct = currentPage.documents[currentPage.size() - 1]
            val nextPage = queryFactByName.startAfter(lastVisibleProduct).get().await()
            isLoading.value = false
            LoadResult.Page(
                data = currentPage.toObjects(FunFact::class.java),
                prevKey = null,
                nextKey = nextPage
            )

        } catch (e: FirebaseFirestoreException) {
            LoadResult.Error(e)
        }
    }


}