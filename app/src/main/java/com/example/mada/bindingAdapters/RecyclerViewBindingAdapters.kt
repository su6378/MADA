package com.example.mada.bindingAdapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mada.adapter.SaveAdapter
import com.example.mada.feature.binder_save.SaveHistory

// 리사이클러뷰의 데이터 바인딩

object RecyclerViewBindingAdapters {
    @BindingAdapter("app:saveList")
    @JvmStatic
    fun RecyclerView.bindImageList(data: List<SaveHistory>) {
        val bindAdapter = this.adapter

        if (bindAdapter is SaveAdapter) {
            if (data.isNotEmpty()) bindAdapter.submitList(data)
        }
    }
}