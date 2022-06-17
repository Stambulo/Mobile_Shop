package com.stambulo.mobileshop.presentation.fragments

import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.stambulo.mobileshop.presentation.image.IImageLoader
import javax.inject.Inject

abstract class BaseFragment<VB, VM>: Fragment(){

    @Inject
    lateinit var imageLoader: IImageLoader<ImageView>
    //TODO: you should make _binding null in onDestroyView in your base fragment
    //TODO: you should call repeatable functions in your base fragment
    protected var _binding: VB? = null
    protected val binding get() = checkNotNull(_binding)
    abstract val viewModel: VM

    //TODO: important functions should be abstract, not open
    open fun setupViewModel() {}
    open fun observeViewModel() {}
    open fun renderLoading() {}
    open fun renderError(error: String) {}
}
