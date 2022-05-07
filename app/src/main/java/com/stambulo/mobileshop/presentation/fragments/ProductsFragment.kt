package com.stambulo.mobileshop.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.stambulo.mobileshop.domain.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.stambulo.mobileshop.databinding.FragmentProductsBinding
import com.stambulo.mobileshop.domain.ProductIntent
import com.stambulo.mobileshop.domain.ProductState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductsFragment: Fragment() {

    private val viewModel: ProductsViewModel by viewModels()
    private var _binding: FragmentProductsBinding? = null
    private val binding get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        observeViewModel()
    }

    private fun setupViewModel(){
        lifecycleScope.launch {
            viewModel.intent.send(ProductIntent.FetchProducts)
        }
    }

    private fun observeViewModel(){
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it){
                    is ProductState.Idle -> {Log.i(">>>", "Idle")}
                    is ProductState.Error -> {Log.i(">>>", "Error")}
                    is ProductState.Loading -> {}
                    is ProductState.Success -> {Log.i(">>>", "Success - ${it.success.body()?.results?.toString()}")}
                }
            }
        }
    }
}
