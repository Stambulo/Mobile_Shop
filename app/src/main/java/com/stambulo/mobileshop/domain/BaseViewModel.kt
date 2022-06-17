package com.stambulo.mobileshop.domain

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel

abstract class BaseViewModel<T>: ViewModel() {
    val intent = Channel<T>(Channel.UNLIMITED)
}
