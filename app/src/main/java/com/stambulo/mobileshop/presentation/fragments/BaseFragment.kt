package com.stambulo.mobileshop.presentation.fragments

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.stambulo.mobileshop.presentation.image.IImageLoader
import javax.inject.Inject

abstract class BaseFragment<VB, VM>: Fragment(){

    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    protected var _binding: VB? = null
    protected val binding get() = checkNotNull(_binding)
    abstract val viewModel: VM

    open fun setupViewModel() {}
    open fun observeViewModel() {}
    open fun renderLoading() {}
    open fun renderError(error: String) {}
}
