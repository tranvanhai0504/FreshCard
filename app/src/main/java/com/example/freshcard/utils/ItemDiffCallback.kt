package com.example.freshcard.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.freshcard.Structure.TopicItem

class ItemDiffCallback(
        private val old: ArrayList<TopicItem>,
        private val new: ArrayList<TopicItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
