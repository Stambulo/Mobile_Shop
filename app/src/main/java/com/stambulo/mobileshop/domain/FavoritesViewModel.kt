package com.stambulo.mobileshop.domain

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.db.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val dbRepository: RoomRepositoryImpl)
    : BaseViewModel<FavoritesIntent>() {

    private val _state = MutableStateFlow<FavoritesState>(FavoritesState.Idle)
    val state: StateFlow<FavoritesState> get() = _state

    init { handleIntent() }

    /********************************************************/
    /**                 Intent Handler                      */
    /********************************************************/
    override fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is FavoritesIntent.SelectItems -> selectItems()
                    is FavoritesIntent.DeleteItems -> deleteItem(it.id)
                    is FavoritesIntent.BackNavigation -> backNavigation()
                    is FavoritesIntent.ToDetailsNavigation -> toDetailsNavigation(it.bundle)
                }
            }
        }
    }

    /********************************************************/
    /**                  Database cases                     */
    /********************************************************/
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

    /********************************************************/
    /**                 Navigation cases                    */
    /********************************************************/
    private fun backNavigation() {
        _state.value = FavoritesState.BackNavigation
    }

    private fun toDetailsNavigation(bundle: Bundle) {
        bundle.putString("source", "DATABASE")
        _state.value = FavoritesState.ToDetailsNavigation(bundle)
    }
}
