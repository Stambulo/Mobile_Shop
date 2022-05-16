package com.stambulo.mobileshop.presentation.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        ProductsAdapter(imageLoader, onListItemClickListener)
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
    }

    private fun sendIntentToViewModel() {
        lifecycleScope.launch {
            if (!isOnline(requireContext())) {             // Internet not connected -> Warning
                binding.refreshButton.visibility = View.VISIBLE
                binding.tvConnect.visibility = View.VISIBLE

            } else {                                       // Internet connected -> send Intent
                viewModel.intent.send(ProductsIntent.FetchProducts)
                binding.refreshButton.visibility = View.GONE
                binding.tvConnect.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is ProductState.Idle -> {}
                    is ProductState.Loading -> {binding.mainProgress.visibility = View.VISIBLE}
                    is ProductState.Success -> { renderSuccess(it.success, it.endOfList) }
                    is ProductState.Error -> {
                        Toast.makeText(requireContext(), "Error - ${it.error}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun renderSuccess(success: List<Results>?, endOfList: Boolean) {
        binding.mainProgress.visibility = View.GONE
        binding.listView.removeFooterView(loadingFooter)
        if (success != null) {
            binding.listView.addFooterView(loadingFooter)
            adapter.updateData(success)
            adapter.notifyDataSetChanged()
            binding.listView.visibility = View.VISIBLE
            if (endOfList){
                binding.listView.removeFooterView(loadingFooter)
            }
        }
    }

    private val onListItemClickListener: OnListItemClickListener =
        object: OnListItemClickListener {
            override fun onItemClick(productId: Int) {
                val bundle = Bundle()
                bundle.putInt("productId", productId)
                Navigation.findNavController(requireActivity(), R.id.nav_host)
                    .navigate(R.id.action_products_to_details, bundle)
            }
        }

    override fun onScroll(
        view: AbsListView?,
        firstVisible: Int,
        visibleItemCount: Int,
        totalItemCount: Int) {
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
