package com.example.mada.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mada.R
import com.example.mada.databinding.ItemSaveBinding
import com.example.mada.feature.binder_save.SaveHistory

class SaveAdapter :
    ListAdapter<SaveHistory, SaveAdapter.SaveHolder>(
        SaveDiffUtil
    ) {
    inner class SaveHolder(private val binding: ItemSaveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: SaveHistory) {
            with(binding) {
                model = data
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SaveHolder {
        return SaveHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_save,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SaveHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object SaveDiffUtil :
        DiffUtil.ItemCallback<SaveHistory>() {
        override fun areItemsTheSame(oldItem: SaveHistory, newItem: SaveHistory): Boolean {
            return oldItem.imageId == newItem.imageId
        }

        override fun areContentsTheSame(
            oldItem: SaveHistory,
            newItem: SaveHistory
        ): Boolean {
            return oldItem == newItem
        }
    }
}