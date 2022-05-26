package com.stambulo.mobileshop.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.mobileshop.R
import com.stambulo.mobileshop.data.db.EntityRoomProduct
import com.stambulo.mobileshop.data.model.Product
import com.stambulo.mobileshop.databinding.FragmentDetailsBinding
import com.stambulo.mobileshop.domain.DetailsIntent
import com.stambulo.mobileshop.domain.DetailsState
import com.stambulo.mobileshop.domain.DetailsViewModel
import com.stambulo.mobileshop.presentation.image.IImageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    private val viewModel: DetailsViewModel by viewModels()
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = checkNotNull(_binding)
    private var productId = 0
    private lateinit var source: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productId = arguments?.getInt("productId")!!
        source = arguments?.getString("source")!!
        observeViewModel()
        setupOnClickListener()
        setIntent()
    }

    /********************************************************/
    /**                  Set Intent Fetch Data              */
    /********************************************************/
    private fun setIntent() {
        lifecycleScope.launch {
            viewModel.intent.send(DetailsIntent.FetchData(productId, source))
        }
    }

    /********************************************************/
    /**             Observe ViewModel                       */
    /********************************************************/
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is DetailsState.Idle -> {}
                    is DetailsState.Loading -> { renderLoading()}
                    is DetailsState.SuccessApi -> { renderSuccessApi(it.success.body()) }
                    is DetailsState.SuccessDatabase -> {renderSuccessDb(it.product)}
                    is DetailsState.Error -> { renderError(it.error) }
                    is DetailsState.NavigateToFavorites -> {navigateToFavorites()}
                    is DetailsState.NavigateToProducts -> {navigateToProducts()}
                }
            }
        }
    }

    /********************************************************/
    /**                   Renders of States                 */
    /********************************************************/
    private fun renderLoading() {
        binding.scrollView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun renderSuccessApi(body: Product?) {
        body?.main_image?.let {
            imageLoader.loadInto(it, binding.detailedImage)
        }
        binding.apply {
            scrollView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            productName.text = body?.name
            productDescription.text = body?.category?.name
            priceFirst.text = "$ " + body?.price.toString()
            priceSecond.text = "$ " + body?.price.toString()
            detailsTv.text = body?.details
        }
    }

    private fun renderSuccessDb(product: EntityRoomProduct) {
        product.image.let {
            imageLoader.loadInto(it, binding.detailedImage)
        }
        binding.apply {
            scrollView.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            productName.text = product.name
            productDescription.text = product.category
            priceFirst.text = "$ " + product.price.toString()
            priceSecond.text = "$ " + product.price.toString()
            detailsTv.text = product.details
        }
    }

    private fun renderError(error: String) {
        Toast.makeText(requireContext(), "Error - $error", Toast.LENGTH_LONG).show()
    }

    /********************************************************/
    /**                   BackPressed                       */
    /********************************************************/
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                Navigation.findNavController(requireActivity(), R.id.nav_host)
                    .navigate(R.id.action_details_to_products)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    /********************************************************/
    /**                  Click Listeners                    */
    /********************************************************/
    private fun setupOnClickListener(){
        binding.toolbar.backArrow.setOnClickListener {
            lifecycleScope.launch {
                viewModel.intent.send(DetailsIntent.BackNavigationIntent)
            }
        }
        binding.toolbar.favorites.setOnClickListener {
            lifecycleScope.launch {
                viewModel.intent.send(DetailsIntent.FavoritesNavigationIntent)
            }
        }
    }

    private fun navigateToProducts(){
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_details_to_products)
    }

    private fun navigateToFavorites(){
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_details_to_favorites)
    }
}