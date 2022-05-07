package com.stambulo.mobileshop.domain

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.api.RepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val repository: RepositoryImpl): ViewModel() {

    val intent = Channel<ProductIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<ProductState>(ProductState.Idle)
    val state: StateFlow<ProductState> get() = _state

    init { handleIntent() }

    private fun handleIntent(){
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {is ProductIntent.FetchProducts -> fetchProduct()}
            }
        }
    }

    private fun fetchProduct(){
        _state.value = ProductState.Loading
        viewModelScope.launch {
            try {
                _state.value = ProductState.Success(repository.getProducts())
            } catch (e: Exception){
                _state.value = ProductState.Error(e.localizedMessage)
            }
        }
    }
}
