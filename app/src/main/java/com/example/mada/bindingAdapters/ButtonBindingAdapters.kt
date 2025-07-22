package com.example.mada.bindingAdapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.example.mada.common.DelayButtonClickListener

object ButtonBindingAdapters {
    @JvmStatic
    @BindingAdapter("onClick")
    fun View.setButtonOnClickListener(listener: View.OnClickListener?) {
        if(listener != null){
            setOnClickListener(DelayButtonClickListener(listener))
        }
    }
}