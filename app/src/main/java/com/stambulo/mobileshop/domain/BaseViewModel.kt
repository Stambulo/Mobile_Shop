package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

open class BaseViewModel<T>: ViewModel() {
    val intent = Channel<T>(Channel.UNLIMITED)

    open fun handleIntent() {}
}
