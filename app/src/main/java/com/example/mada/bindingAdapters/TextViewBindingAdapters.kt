package com.example.mada.bindingAdapters

import androidx.databinding.BindingAdapter
import com.nitish.typewriterview.TypeWriterView

object TextViewBindingAdapters {
    @JvmStatic
    @BindingAdapter("app:animateText")
    fun TypeWriterView.setAnimateText(text: String) {
        this.animateText(text)
    }
}