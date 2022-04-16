package com.amg.instabugtask.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amg.instabugtask.databinding.ListitemWordBinding
import com.amg.instabugtask.models.Word

class WordViewHolder(private val item: ListitemWordBinding) : RecyclerView.ViewHolder(item.root) {

    fun bind(word: Word) {
        item.word.text = word.chars
        item.count.text = word.count.toString()
    }

    companion object {
        fun create(parent: ViewGroup, ): WordViewHolder {
            return WordViewHolder(
                ListitemWordBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}