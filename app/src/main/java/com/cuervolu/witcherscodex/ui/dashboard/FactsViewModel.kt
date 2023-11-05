package com.cuervolu.witcherscodex.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cuervolu.witcherscodex.data.network.FirebaseClient
import com.cuervolu.witcherscodex.domain.models.FunFact
import com.google.firebase.firestore.Query.Direction.ASCENDING
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class FactsViewModel @Inject constructor(firebaseClient: FirebaseClient) : ViewModel() {
    private val pageSize = 10

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> get() = _isLoading

    private val queryFactByName = firebaseClient.db.collection("funFacts").orderBy("title", ASCENDING).limit(pageSize.toLong())

    val flow: Flow<PagingData<FunFact>> = Pager(PagingConfig(pageSize)) {
        FunFactsPagingSource(queryFactByName, _isLoading)
    }.flow.cachedIn(viewModelScope)

}