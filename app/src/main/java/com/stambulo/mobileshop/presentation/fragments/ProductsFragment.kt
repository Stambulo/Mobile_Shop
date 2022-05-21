package com.stambulo.mobileshop.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        loadingFooter = layoutInflater.inflate(R.layout.footer_loading_next_page, null)
        binding.listView.setOnScrollListener(this)
        binding.listView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refreshButton.setOnClickListener { sendIntentToViewModel() }
        sendIntentToViewModel()
        observeViewModel()
        setupOnClickListener()
    }

    private fun setupOnClickListener() {
        binding.toolbar.favorites.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_products_to_favorites)
        }
    }

    private val adapterClickListener: OnListItemClickListener =
        object : OnListItemClickListener {
            /**
             *    Click on Item -> Go to details fragment
             */
            override fun onItemClick(productId: Int) {
                val bundle = Bundle()
                bundle.putInt("productId", productId)
                Navigation.findNavController(requireActivity(), R.id.nav_host)
                    .navigate(R.id.action_products_to_details, bundle)
            }

            /**
             *   Click on empty heart -> add product to favorites
             */
            override fun onAddToFavoritesClick(
                product: Results,
                position: Int,
                itemView: View?,
                parent: ViewGroup
            ) {
                lifecycleScope.launch {
                    viewModel.intent.send(
                        ProductsIntent.InsertProductIntoDb(
                            product,
                            position,
                            itemView,
                            parent)
                    )
                }
            }
            /**
             *   Click on filled heart -> remove product from favorites
             */
            override fun onRemoveFromFavoritesClick(
                id: Int,
                position: Int,
                itemView: View?,
                parent: ViewGroup
            ) {
                lifecycleScope.launch{
                    viewModel.intent.send(
                        ProductsIntent.RemoveFromFavorites(
                            id,
                            position,
                            itemView,
                            parent)
                    )
                }
            }
        }

    private fun sendIntentToViewModel() {
        lifecycleScope.launch {
            /**
             *    Internet not connected -> Warning message
             */
            if (!isOnline(requireContext())) {
                binding.refreshButton.visibility = View.VISIBLE
                binding.tvConnect.visibility = View.VISIBLE

            } else {
                /**
                 *   Internet connected -> send Intent to ViewModel
                 */
                viewModel.intent.send(ProductsIntent.FetchProducts)
                binding.refreshButton.visibility = View.GONE
                binding.tvConnect.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.productState.collect {
                when (it) {
                    is ProductState.Idle -> {}
                    is ProductState.Loading -> {
                        binding.mainProgress.visibility = View.VISIBLE
                    }
                    is ProductState.UpdateIndices -> {
                        updateItemView(it.indices, it.position, it.itemView, it.parent)
                    }
                    is ProductState.Success -> {
                        renderSuccess(it.success, it.endOfList, it.indices)
                    }
                    is ProductState.Error -> {
                        Toast.makeText(requireContext(), "Error - ${it.error}", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

    private fun updateItemView(
        indices: List<Int>,
        position: Int,
        itemView: View?,
        parent: ViewGroup
    ) {
        adapter.updateItemView(indices, position, itemView, parent)
        adapter.notifyDataSetChanged()
    }

    private fun renderSuccess(success: List<Results>?, endOfList: Boolean, indices: List<Int>) {
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
            sendIntentToViewModel()
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) -> return true
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) -> return true
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) -> return true
            }
        }
        return false
    }
}
