package com.stambulo.mobileshop.domain

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stambulo.mobileshop.data.api.RepositoryImpl
import com.stambulo.mobileshop.data.db.RoomRepositoryImpl
import com.stambulo.mobileshop.data.model.Results
import com.stambulo.mobileshop.data.model.resultToRoomConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: RepositoryImpl,
    private val dbRepository: RoomRepositoryImpl
) : ViewModel() {

    private var isConnected = true
    private var listOfProducts: MutableList<Results> = mutableListOf()
    private var pager = Pager(23, 0, 1, 3, 10)  // Start position
    val intent = Channel<ProductsIntent>(Channel.UNLIMITED)
    private val _productState = MutableStateFlow<ProductState>(ProductState.Idle)
    val productState: StateFlow<ProductState> get() = _productState

    init {
        handleIntent()
    }

    /********************************************************/
    /**                 Intent Handler                      */
    /********************************************************/
    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                when (it) {
                    is ProductsIntent.ConnectionChanged -> onChangeConnection(it.isOnline)
                    is ProductsIntent.GoToDetails -> navigateToDetails(it.bundle)
                    is ProductsIntent.GoToFavorites -> navigateToFavorites()
                    is ProductsIntent.FetchProducts -> getNextProductPage()
                    is ProductsIntent.InsertProductIntoDb -> insertProductIntoDb(
                        it.product, it.position, it.itemView, it.parent
                    )
                    is ProductsIntent.RemoveFromDB -> removeProductFromDB(
                        it.id, it.position, it.itemView, it.parent
                    )
                }
            }
        }
    }

    /********************************************************/
    /**                 Next Page Loader                    */
    /********************************************************/
    private fun getNextProductPage() {
        if (isConnected) {
            viewModelScope.launch {
                if (pager.nextPage <= pager.pages) {     // If not the end of list -> Load next page
                    _productState.value = ProductState.Loading           // LOADING STATE
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
                        if (pager.nextPage > pager.pages) {            // End of list -> hide footer
                            endOfList = true
                        }
                        _productState.value = ProductState.Success(      //  SUCCESS STATE
                            listOfProducts,
                            endOfList,
                            readAllIdFromDb()
                        )
                    } catch (e: Exception) {                             // ERROR STATE
                        _productState.value = e.localizedMessage?.let { ProductState.Error(it) }!!
                    }
                }
            }
        }
    }

    /********************************************************/
    /**                Connection cases                     */
    /********************************************************/
    private fun onChangeConnection(online: Boolean) {
        isConnected = online
        if (!online) {
            _productState.value = ProductState.LostConnection
        } else {
            _productState.value = ProductState.RestoreConnection
        }
    }

    /********************************************************/
    /**                 Navigation cases                    */
    /********************************************************/
    private fun navigateToFavorites() {
        _productState.value = ProductState.NavigateToFavorites
    }

    private fun navigateToDetails(bundle: Bundle) {
        if (isConnected) {
            bundle.putString("source", "API")
            _productState.value = ProductState.NavigateToDetails(bundle)
        } else {
            _productState.value = ProductState.LostConnection
        }
    }

    /********************************************************/
    /**                  Database cases                     */
    /********************************************************/
    private fun insertProductIntoDb(
        product: Results, position: Int, itemView: View?, parent: ViewGroup
    ) {
        viewModelScope.launch {
            dbRepository.insertData(resultToRoomConverter(product))
            _productState.value = ProductState.UpdateItemView(
                readAllIdFromDb(), position, itemView, parent
            )
        }
    }

    private fun removeProductFromDB(
        id: Int, position: Int, itemView: View?, parent: ViewGroup
    ) {
        viewModelScope.launch {
            dbRepository.deleteById(id)
            _productState.value = ProductState.UpdateItemView(
                readAllIdFromDb(), position, itemView, parent
            )
        }
    }

    private suspend fun readAllIdFromDb(): List<Int> {
        return dbRepository.getIdListFromDb()
    }
}

/** Pager class */
data class Pager(
    var count: Int,
    var page: Int,
    var nextPage: Int,
    var pages: Int,
    var per_page: Int
)
