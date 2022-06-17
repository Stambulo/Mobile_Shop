package com.stambulo.mobileshop.domain

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.stambulo.mobileshop.data.model.Results

data class ProductState(
    val type: Type = Type.IDLE,
    val errorMessage: String = "",
    val bundle: Bundle? = null,
    val lastPage: Boolean = false,
    val success: Success? = null,
    val updateView: UpdateItemView? = null
){
    enum class Type {
        IDLE,
        LOADING,
        Error,
        Success,
        UpdateItemView,
        LostConnection,
        RestoreConnection,
        NavigateToFavorites,
        NavigateToDetails}

    //TODO: using emptyList as default is more preferable then nullable list
    data class Success(
        val success: List<Results>?,
        val endOfList: Boolean,
        val indices: List<Int>)

    data class UpdateItemView(
        val indices: List<Int>,
        val position: Int,
        val itemView: View?,
        val parent: ViewGroup)
}
