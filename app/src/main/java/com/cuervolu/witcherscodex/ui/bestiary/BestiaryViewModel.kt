package com.cuervolu.witcherscodex.ui.bestiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.cuervolu.witcherscodex.domain.models.Bestiary
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class BestiaryViewModel @Inject constructor(private val queryMonstersByName: Query) :
    ViewModel() {
    private val pageSize = 10

    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> get() = _isLoading


    val flow: Flow<PagingData<Bestiary>> = Pager(PagingConfig(pageSize)) {
        BestiaryPagingSource(queryMonstersByName, _isLoading)
    }.flow.cachedIn(viewModelScope)


}