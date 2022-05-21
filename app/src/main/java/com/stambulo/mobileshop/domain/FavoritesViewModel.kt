package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.db.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val dbRepository: RoomRepositoryImpl)
    : ViewModel() {

    val intent = Channel<FavoritesIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val state: StateFlow<FavoritesState> get() = _state

    init { handleIntent() }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is FavoritesIntent.SelectItems -> selectItems()
                    is FavoritesIntent.DeleteItems -> deleteItem(it.id)
                }
            }
        }
    }

    private fun deleteItem(id: Int) {
        viewModelScope.launch {
            dbRepository.deleteById(id)
            selectItems()
        }
    }

    private fun selectItems(){
        viewModelScope.launch {
            _state.value = FavoritesState.Success(dbRepository.getDataFromDB())
        }
    }
}
