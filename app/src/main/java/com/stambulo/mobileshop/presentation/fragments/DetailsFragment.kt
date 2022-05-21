package com.stambulo.mobileshop.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.mobileshop.R
import com.stambulo.mobileshop.data.model.Product
import com.stambulo.mobileshop.databinding.FragmentDetailsBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productId = arguments?.getInt("productId")!!
        observeViewModel()
        viewModel.getProductById(productId)
        setupOnClickListener()
    }

    private fun setupOnClickListener(){
        binding.toolbar.backArrow.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_details_to_products)
        }
        binding.toolbar.favorites.setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.nav_host)
                .navigate(R.id.action_details_to_favorites)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is DetailsState.Idle -> {}
                    is DetailsState.Loading -> { renderLoading()}
                    is DetailsState.Success -> { renderSuccess(it.success.body()) }
                    is DetailsState.Error -> { Log.i(">>>", "Error - $it") }
                }
            }
        }
    }

    private fun renderLoading() {
        binding.scrollView.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun renderSuccess(body: Product?) {
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
}
