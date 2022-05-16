package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.api.RepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: RepositoryImpl): ViewModel() {

    private val _state = MutableStateFlow<DetailsState>(DetailsState.Idle)
    val state: StateFlow<DetailsState> get() = _state

    fun getProductById(id: Int) {
        viewModelScope.launch {
            _state.value = DetailsState.Loading
            try {
                _state.value = DetailsState.Success(repository.getProducts(id))
            } catch (e: Exception) {
                _state.value = e.localizedMessage?.let { DetailsState.Error(it) }!!
            }
        }
    }
}
