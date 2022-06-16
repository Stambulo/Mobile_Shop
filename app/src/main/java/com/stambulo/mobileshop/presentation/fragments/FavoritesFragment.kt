package com.stambulo.mobileshop.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.stambulo.mobileshop.R
import com.stambulo.mobileshop.data.db.EntityRoomProduct
import com.stambulo.mobileshop.databinding.FragmentFavoritesBinding
import com.stambulo.mobileshop.domain.FavoritesIntent
import com.stambulo.mobileshop.domain.FavoritesState
import com.stambulo.mobileshop.domain.FavoritesViewModel
import com.stambulo.mobileshop.presentation.adapters.FavoritesAdapter
import com.stambulo.mobileshop.presentation.adapters.OnFavoritesClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesFragment: BaseFragment<FragmentFavoritesBinding, FavoritesViewModel>() {

    override val viewModel: FavoritesViewModel by viewModels()
    private val adapter by lazy(LazyThreadSafetyMode.NONE) {
        FavoritesAdapter(imageLoader, adapterClickListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        binding.favoritesListView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeViewModel()
        backArrowClickListener()
    }

    /********************************************************/
    /**          Setup and Observe ViewModel                */
    /********************************************************/
    override fun setupViewModel() {
        lifecycleScope.launch {
            viewModel.intent.send(FavoritesIntent.SelectItems)
        }
    }

    override fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is FavoritesState.Idle -> {}
                    is FavoritesState.Success -> { renderSuccess(it.success) }
                    is FavoritesState.BackNavigation -> {backNavigation()}
                    is FavoritesState.ToDetailsNavigation -> {toDetails(it.bundle)}
                }
            }
        }
    }

    /********************************************************/
    /**                 Success Render                      */
    /********************************************************/
    private fun renderSuccess(success: List<EntityRoomProduct>) {
        adapter.updateData(success)
        adapter.notifyDataSetChanged()
        binding.favoritesListView.visibility = View.VISIBLE
    }

    /********************************************************/
    /**                      Navigation                     */
    /********************************************************/
    private fun backNavigation() {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_favorites_to_products)
    }

    private fun toDetails(bundle: Bundle) {
        Navigation.findNavController(requireActivity(), R.id.nav_host)
            .navigate(R.id.action_favorites_to_details, bundle)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                Navigation.findNavController(requireActivity(), R.id.nav_host)
                    .navigate(R.id.action_favorites_to_products)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            callback
        )
    }

    /********************************************************/
    /**                 Click Listeners                     */
    /********************************************************/
    private fun backArrowClickListener() {
        binding.toolbar.backArrow.setOnClickListener {
            lifecycleScope.launch {
                viewModel.intent.send(FavoritesIntent.BackNavigation)
            }
        }
    }

    private val adapterClickListener: OnFavoritesClickListener =
        object : OnFavoritesClickListener {
            override fun onItemClick(productId: Int) {
                val bundle = Bundle()
                bundle.putInt("productId", productId)
                lifecycleScope.launch {
                    viewModel.intent.send(FavoritesIntent.ToDetailsNavigation(bundle))
                }
            }
            override fun onHeartClick(productId: Int) {
                lifecycleScope.launch {
                    viewModel.intent.send(FavoritesIntent.DeleteItems(productId))
                }
            }
        }
}
