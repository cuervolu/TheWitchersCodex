package com.cuervolu.witcherscodex.ui.bestiary

import com.cuervolu.witcherscodex.domain.models.Bestiary

sealed class BestiaryViewState {
    data object Loading : BestiaryViewState()
    data class Success(val data: List<Bestiary>) : BestiaryViewState()
    data class Error(val errorMessage: String) : BestiaryViewState()
}
