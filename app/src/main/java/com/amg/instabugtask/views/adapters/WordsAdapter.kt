package com.amg.instabugtask.views.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amg.instabugtask.models.Word

class WordsAdapter(private val words: List<Word>) : RecyclerView.Adapter<WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(words[position])
    }

    override fun getItemCount(): Int {
        return words.size
    }
}