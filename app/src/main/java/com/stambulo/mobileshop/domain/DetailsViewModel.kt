package com.stambulo.mobileshop.domain

import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.api.RepositoryImpl
import com.stambulo.mobileshop.data.db.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: RepositoryImpl,
    private val dbRepository: RoomRepositoryImpl): BaseViewModel<DetailsIntent>() {

    private val _state = MutableStateFlow<DetailsState>(DetailsState.Idle)
    val state: StateFlow<DetailsState> get() = _state
    private var source = "API"

    init {
        handleIntent()
    }

    /********************************************************/
    /**                 Intent Handler                      */
    /********************************************************/
    override fun handleIntent(){
        viewModelScope.launch {
            intent.consumeAsFlow().collect{
                when (it){
                    is DetailsIntent.Idle -> {}
                    is DetailsIntent.FetchData -> {fetchData(it.productId, it.source)}
                    is DetailsIntent.BackNavigationIntent -> {backNavigation()}
                    is DetailsIntent.FavoritesNavigationIntent -> {navigateToFavorites()}
                }
            }
        }
    }

    /********************************************************/
    /**                 Get Data Cases                      */
    /********************************************************/
    private fun fetchData(productId: Int, source: String) {
        when (source){
            "DATABASE" -> {getDataFromDb(productId)}
            "API" -> {getDataFromApi(productId)}
        }
    }

    private fun getDataFromDb(id: Int){
        source = "DATABASE"
        _state.value = DetailsState.Loading
        viewModelScope.launch {
            try {
                _state.value = DetailsState.SuccessDatabase(dbRepository.getProductById(id))
            } catch (e: Exception){
                _state.value = e.localizedMessage?.let { DetailsState.Error(it) }!!
            }
        }
    }

    private fun getDataFromApi(id: Int){
        source = "API"
        _state.value = DetailsState.Loading
        viewModelScope.launch {
            try {
                _state.value = DetailsState.SuccessApi(repository.getProducts(id))
            } catch (e: Exception) {
                _state.value = e.localizedMessage?.let { DetailsState.Error(it) }!!
            }
        }
    }

    /********************************************************/
    /**                 Navigation Cases                    */
    /********************************************************/
    private fun backNavigation() {
        when (source){
            "API" -> {navigateToProducts()}
            "DATABASE" -> {navigateToFavorites()}
        }
    }

    private fun navigateToFavorites(){
        _state.value = DetailsState.NavigateToFavorites
    }

    private fun navigateToProducts(){
        _state.value = DetailsState.NavigateToProducts
    }
}
