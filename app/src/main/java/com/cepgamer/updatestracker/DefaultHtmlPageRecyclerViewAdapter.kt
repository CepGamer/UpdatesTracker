package com.cepgamer.updatestracker

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.cepgamer.updatestracker.placeholder.PlaceholderContent.PlaceholderItem
import com.cepgamer.updatestracker.databinding.HtmlPageFragmentItemBinding
import com.cepgamer.updatestracker.model.RawHtmlEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import java.time.ZoneId

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class DefaultHtmlPageRecyclerViewAdapter(
    private val values: List<RawHtmlEntity>
) : RecyclerView.Adapter<DefaultHtmlPageRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            HtmlPageFragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rawHtml = values[position]
        holder.apply {
            addressTextView.text = rawHtml.address
            lastUpdatedTextView.text =
                rawHtml.lastUpdate.atZone(ZoneId.systemDefault()).toLocalTime().toString()
            lastCheckedTextView.text =
                rawHtml.lastCheck.atZone(ZoneId.systemDefault()).toLocalTime().toString()
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: HtmlPageFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val addressTextView = binding.htmlAddress
        val lastUpdatedTextView = binding.lastUpdatedTime
        val lastCheckedTextView = binding.lastCheckedTime

        override fun toString(): String {
            return "${super.toString()}\n$addressTextView"
        }
    }

}