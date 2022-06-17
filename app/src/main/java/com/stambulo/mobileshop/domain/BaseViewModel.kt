package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

//TODO: base classes should be abstract, not open
open class BaseViewModel<T>: ViewModel() {
    val intent = Channel<T>(Channel.UNLIMITED)

    open fun handleIntent() {}
}
