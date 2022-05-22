package com.stambulo.mobileshop.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.mobileshop.R
import com.stambulo.mobileshop.data.model.Results
import com.stambulo.mobileshop.databinding.FragmentProductsBinding
import com.stambulo.mobileshop.domain.ProductState
import com.stambulo.mobileshop.domain.ProductsIntent
import com.stambulo.mobileshop.domain.ProductsViewModel
import com.stambulo.mobileshop.presentation.adapters.OnListItemClickListener
import com.stambulo.mobileshop.presentation.adapters.ProductsAdapter
import com.stambulo.mobileshop.presentation.image.IImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProductsFragment : Fragment(), AbsListView.OnScrollListener {

    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    private lateinit var loadingFooter: View
    private val viewModel: ProductsViewModel by viewModels()
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = checkNotNull(_binding)
    private var visibleLastIndex = 0
    private var visibleItemCount = 0
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        ProductsAdapter(imageLoader, adapterClickListener)
    }

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        loadingFooter = layoutInflater.inflate(R.layout.footer_loading_next_page, null)
        binding.listView.setOnScrollListener(this)
        binding.listView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshButton.setOnClickListener { setIntent() }
        isOnline()
        setIntent()
        observeViewModel()
        favoritesClickListener()
    }

    /********************************************************/
    /**                       Intent                        */
    /********************************************************/
    private fun setIntent() {
        lifecycleScope.launch {
            viewModel.intent.send(ProductsIntent.FetchProducts)
        }
    }

    /********************************************************/
    /**             Observe ViewModel                       */
    /********************************************************/
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productState.collect {
                when (it) {
                    is ProductState.Idle -> {}
                    is ProductState.Loading -> {
                        renderLoading()
                    }
                    is ProductState.UpdateItemView -> {
                        renderUpdateItemView(it.indices, it.position, it.itemView, it.parent)
                    }
                    is ProductState.Success -> {
                        renderSuccess(it.success, it.endOfList, it.indices)
                    }
                    is ProductState.Error -> {
                        renderError(it.error)
                    }
                    is ProductState.LostConnection -> {renderLostConnection()}
                    is ProductState.RestoreConnection -> {renderRestoreConnection()}
                    is ProductState.NavigateToDetails -> {goToDetailsScreen(it.bundle)}
                    is ProductState.NavigateToFavorites -> {goToFavoritesScreen()}
                }
            }
        }
    }

    /********************************************************/
    /**                Renders of States                    */
    /********************************************************/
    private fun renderSuccess(success: List<Results>?, endOfList: Boolean, indices: List<Int>) {
        binding.refreshButton.visibility = View.GONE
        binding.tvConnect.visibility = View.GONE
        binding.listView.alpha = 1F
        binding.mainProgress.visibility = View.GONE
        binding.listView.removeFooterView(loadingFooter)
        if (success != null) {
            binding.listView.visibility = View.VISIBLE
            binding.listView.addFooterView(loadingFooter)
            adapter.updateList(success, indices)
            adapter.notifyDataSetChanged()
            if (endOfList) {
                binding.listView.removeFooterView(loadingFooter)
            }
        }
    }

    private fun renderLoading() {
        binding.mainProgress.visibility = View.VISIBLE
    }

    private fun renderError(error: String) {
        Toast.makeText(requireContext(), "Error - $error", Toast.LENGTH_LONG).show()
    }

    private fun renderRestoreConnection() {
        /** Internet connected -> Hide Warning message */
        binding.listView.visibility = View.VISIBLE
        binding.listView.alpha = 1F
        binding.refreshButton.visibility = View.GONE
        binding.tvConnect.visibility = View.GONE
        setIntent()
    }

    private fun renderLostConnection() {
        /** Internet not connected -> Warning message */
        binding.listView.alpha = 0.3F
        binding.refreshButton.visibility = View.VISIBLE
        binding.tvConnect.visibility = View.VISIBLE
    }

    /**     Show and Hide hearts in main feed   */
    private fun renderUpdateItemView(
        indices: List<Int>, position: Int, itemView: View?, parent: ViewGroup) {
        adapter.updateItemView(indices, position, itemView, parent)
        adapter.notifyDataSetChanged()
    }

    /********************************************************/
    /**                      Navigation                     */
    /********************************************************/
    private fun goToDetailsScreen(bundle: Bundle) {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_products_to_details, bundle)
    }

    private fun goToFavoritesScreen() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_products_to_favorites)
    }

    /********************************************************/
    /**                  Click Listeners                    */
    /********************************************************/
    private fun favoritesClickListener() {
        binding.toolbar.favorites.setOnClickListener {
            lifecycleScope.launch {
                viewModel.intent.send(ProductsIntent.GoToFavorites)
            }
        }
    }

    private val adapterClickListener: OnListItemClickListener =
        object : OnListItemClickListener {
            /** Click on Item -> Go to details fragment */
            override fun onItemClick(productId: Int) {
                val bundle = Bundle()
                bundle.putInt("productId", productId)
                lifecycleScope.launch {
                    viewModel.intent.send(
                        ProductsIntent.GoToDetails(bundle)
                    )
                }
            }
            /** Click on empty heart -> add product to favorites */
            override fun onAddToFavoritesClick(
                product: Results, position: Int, itemView: View?, parent: ViewGroup
            ) {
                lifecycleScope.launch {
                    viewModel.intent.send(
                        ProductsIntent.InsertProductIntoDb(
                            product, position, itemView, parent)
                    )
                }
            }
            /** Click on filled heart -> remove product from favorites */
            override fun onRemoveFromFavoritesClick(
                id: Int, position: Int, itemView: View?, parent: ViewGroup
            ) {
                lifecycleScope.launch{
                    viewModel.intent.send(
                        ProductsIntent.RemoveFromDB(
                            id, position, itemView, parent)
                    )
                }
            }
        }

    /********************************************************/
    /**                Scroll Listeners                     */
    /********************************************************/
    override fun onScroll(
        view: AbsListView?, firstVisible: Int, visibleItemCount: Int, totalItemCount: Int
    ) {
        this.visibleItemCount = visibleItemCount
        visibleLastIndex = firstVisible + visibleItemCount - 1
    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        val itemsLastIndex = adapter.count - 1
        val lastIndex = itemsLastIndex + 1
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
            setIntent()
        }
    }

    /********************************************************/
    /**                 isOnline Listeners                  */
    /********************************************************/
    private fun isOnline(): Boolean {
        val context = requireContext()
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }
        if (capabilities != null) {
            when {
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) -> return true
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) -> return true
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> return true
            }
            restoreConnection()
        }
        lostConnection()
        return false
    }

    private var networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // network available
            restoreConnection()
        }
        override fun onLost(network: Network) {
            // network unavailable
            lostConnection()
        }
    }

    private fun restoreConnection(){
        lifecycleScope.launch {
            viewModel.intent.send(ProductsIntent.ConnectionChanged(true))
        }
    }

    private fun lostConnection(){
        lifecycleScope.launch {
            viewModel.intent.send(ProductsIntent.ConnectionChanged(false))
        }
    }
}
