package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.api.RepositoryImpl
import com.stambulo.mobileshop.data.model.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val repository: RepositoryImpl) : ViewModel() {

    private var listOfProducts: MutableList<Results> = mutableListOf()
    private var pager = Pager(23, 0, 1, 3, 10)     // Start pager
    val intent = Channel<ProductsIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<ProductState>(ProductState.Idle)
    val state: StateFlow<ProductState> get() = _state

    init { handleIntent() }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is ProductsIntent.FetchProducts -> getNextProductPage()
                }
            }
        }
    }

    private fun getNextProductPage() {
        viewModelScope.launch {
            if (pager.nextPage <= pager.pages) {        // Not the end of list -> Load next page
                _state.value = ProductState.Loading                                 // LOADING
                try {
                    val result = repository.getProductsPage(pager.nextPage, pager.per_page)
                    val products = result.body()?.results
                    if (products != null) {
                        for (i in products.listIterator()) {
                            listOfProducts.add(i)
                        }
                    }
                    pager.count = result.body()?.count!!
                    pager.page = result.body()?.current_page!!
                    pager.nextPage = result.body()?.current_page!! + 1
                    pager.pages = result.body()?.total_pages!!
                    pager.per_page = result.body()?.per_page!!
                    var endOfList = false
                    if (pager.nextPage > pager.pages) {              // End of list -> hide footer
                        endOfList = true
                    }
                    _state.value = ProductState.Success(listOfProducts, endOfList)       //  SUCCESS
                } catch (e: Exception) {
                    _state.value = e.localizedMessage?.let { ProductState.Error(it) }!!  // ERROR
                }
            }
        }
    }
}

data class Pager(
    var count: Int,
    var page: Int,
    var nextPage: Int,
    var pages: Int,
    var per_page: Int
)
