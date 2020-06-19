package com.groodysoft.tdaexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.groodysoft.tdaexample.fragment.GetQuotesFragment
import com.groodysoft.tdaexample.TDAQuote
import com.groodysoft.tdaexample.adapterRowColor
import com.groodysoft.tdaexample.databinding.ListItemQuoteBinding

/**
 * Adapter for the [RecyclerView] in [GetQuotesFragment].
 */
class QuoteAdapter : ListAdapter<TDAQuote, QuoteAdapter.ViewHolder>(QuoteDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val track = getItem(position)
        holder.apply {
            bind(track)
            itemView.tag = track
            itemView.setBackgroundColor(adapterRowColor(position))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListItemQuoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    class ViewHolder(
        private val binding: ListItemQuoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TDAQuote) {
            binding.apply {
                quote = item
                executePendingBindings()
            }
        }
    }
}

class QuoteDiffCallback : DiffUtil.ItemCallback<TDAQuote>() {

    override fun areItemsTheSame(oldItem: TDAQuote, newItem: TDAQuote): Boolean {
        return oldItem.symbol == newItem.symbol
    }

    override fun areContentsTheSame(oldItem: TDAQuote, newItem: TDAQuote): Boolean {
        return oldItem == newItem
    }
}
