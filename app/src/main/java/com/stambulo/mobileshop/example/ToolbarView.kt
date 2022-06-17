package com.stambulo.mobileshop.example

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewbinding.ViewBinding
import com.stambulo.mobileshop.R
import com.stambulo.mobileshop.databinding.ActivityMainBinding

//TODO: EXAMPLE, do not use, make your own
class ToolbarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

     val viewBinding: ViewBinding = TODO("binding as in example below")

    /*ViewToolbarBinding.inflate(
        LayoutInflater.from(context), this,
        true
    )*/

    init {
        attrs?.let {
            initAttributes(context, attrs)
        }
    }

    private fun initAttributes(context: Context, attrs: AttributeSet) {
        val attr: TypedArray = getTypedArray(context, attrs, R.styleable.Toolbar)

        viewBinding.apply {
            //TODO: configure using
            attr.getResourceId(
                R.styleable.Toolbar_toolbarLeftImage,
                R.drawable.ic_user
            )
        }
    }

    private fun getTypedArray(
        context: Context,
        attributeSet: AttributeSet,
        attr: IntArray
    ): TypedArray {
        return context.obtainStyledAttributes(attributeSet, attr, 0, 0)
    }
}