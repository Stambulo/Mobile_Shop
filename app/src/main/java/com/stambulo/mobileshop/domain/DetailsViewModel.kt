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

//TODO: viewModel is NOT A PART of domain
//TODO: format your code properly with REFORMAT CODE tool
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
                    is DetailsIntent.Idle -> {} //TODO: use UNIT instead of {}
                    is DetailsIntent.FetchData -> {fetchData(it.productId, it.source)} //TODO: if there is only one operator, you can omit { }
                    is DetailsIntent.BackNavigationIntent -> backNavigation() //TODO: like here
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

    //TODO: you repeat yourself multiple times. You should create some extensions and sealed classes to handle extensions, loading and etc.
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
