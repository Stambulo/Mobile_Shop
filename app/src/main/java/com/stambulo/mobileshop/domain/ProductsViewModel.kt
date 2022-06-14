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

    private var lastPage = false
    private var isConnected = true
    private var listOfProducts: MutableList<Results> = mutableListOf()
    private var pager = Pager(0, 0, 0, 0)
    val intent = Channel<ProductsIntent>(Channel.UNLIMITED)
    private val _productState = MutableStateFlow(ProductState(ProductState.Type.IDLE))
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
                    is ProductsIntent.FetchProducts -> loader()
                    is ProductsIntent.InsertProductIntoDb -> insertProductIntoDb(
                        it.product, it.position, it.itemView, it.parent)
                    is ProductsIntent.RemoveFromDB -> removeProductFromDB(
                        it.id, it.position, it.itemView, it.parent)
                }
            }
        }
    }

    /********************************************************/
    /**                 Next Page Loader                    */
    /********************************************************/
    private fun loader() {
        if (!isConnected) {
            _productState.value = ProductState(ProductState.Type.LostConnection)
            return
        }
        if (pager.items == 0){                                      // First load -> Load first page
            pager.page = 1
            pager.per_page = 10
            loadData(pager.page, pager.per_page,  lastPage)
        }
        if (pager.items > 0 && (pager.page + 1) <= pager.pages){
            lastPage = (pager.page + 1) == pager.pages
            loadData(pager.page + 1, pager.per_page, lastPage)
        }
    }

    private fun loadData(page: Int, page_size: Int, endOfList: Boolean) {
        _productState.value = ProductState(ProductState.Type.LOADING)          // LOADING STATE
        viewModelScope.launch {
            try {
                val result = repository.getProductsPage(page, page_size)
                val products = result.body()?.results
                if (products != null) {
                    for (i in products.listIterator()) {
                        listOfProducts.add(i)
                    }
                }
                pager.items = result.body()?.count!!
                pager.page = result.body()?.current_page!!
                pager.pages = result.body()?.total_pages!!
                pager.per_page = result.body()?.per_page!!
                _productState.value = ProductState(ProductState.Type.Success,   // SUCCESS
                    success = ProductState.Success(
                        listOfProducts,
                        endOfList,
                        readAllIdFromDb()
                    )
                )
            } catch (e: Exception) {                                            // ERROR STATE
                _productState.value = ProductState(
                    ProductState.Type.Error,
                    errorMessage = e.localizedMessage!!
                )
            }
        }
    }

    /********************************************************/
    /**                Connection cases                     */
    /********************************************************/
    private fun onChangeConnection(online: Boolean) {
        isConnected = online
        if (!online) {
            isConnected = false
            _productState.value = ProductState(ProductState.Type.LostConnection)
        } else {
            isConnected = true
            _productState.value = ProductState(
                ProductState.Type.RestoreConnection,
                lastPage = lastPage
            )
            if (pager.page == 0){
                loader()
            }
        }
    }

    /********************************************************/
    /**                 Navigation cases                    */
    /********************************************************/
    private fun navigateToFavorites() {
        _productState.value = ProductState(ProductState.Type.NavigateToFavorites)
    }

    private fun navigateToDetails(bundle: Bundle) {
        if (isConnected) {
            bundle.putString("source", "API")
            _productState.value = ProductState(
                ProductState.Type.NavigateToDetails,
                bundle = bundle
            )
        } else {
            _productState.value = ProductState(ProductState.Type.LostConnection)
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
            _productState.value = ProductState(ProductState.Type.UpdateItemView,
                updateView = ProductState.UpdateItemView(
                    readAllIdFromDb(), position, itemView, parent
                )
            )
        }
    }

    private fun removeProductFromDB(
        id: Int, position: Int, itemView: View?, parent: ViewGroup
    ) {
        viewModelScope.launch {
            dbRepository.deleteById(id)
            _productState.value = ProductState(ProductState.Type.UpdateItemView,
                updateView = ProductState.UpdateItemView(
                    readAllIdFromDb(), position, itemView, parent
                )
            )
        }
    }

    private suspend fun readAllIdFromDb(): List<Int> {
        return dbRepository.getIdListFromDb()
    }
}

/** Pager class */
data class Pager(
    var items: Int,
    var page: Int,
    var pages: Int,
    var per_page: Int
)
