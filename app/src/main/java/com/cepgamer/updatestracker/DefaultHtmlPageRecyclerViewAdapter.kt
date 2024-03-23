package com.cepgamer.updatestracker

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cepgamer.updatestracker.databinding.HtmlPageFragmentItemBinding
import com.cepgamer.updatestracker.model.RawHtmlEntity
import java.time.ZoneId

class DefaultHtmlPageRecyclerViewAdapter(
    private val values: List<RawHtmlEntity>,
    private val onLongClickListener: (String) -> OnLongClickListener,
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
        Log.i(javaClass.name, "ViewHolder bound: $position")
        val rawHtml = values[position]
        holder.apply {
            addressTextView.text = rawHtml.address
            lastUpdatedTextView.text =
                rawHtml.lastUpdate.atZone(ZoneId.systemDefault()).toLocalTime().toString()
            lastCheckedTextView.text =
                rawHtml.lastCheck.atZone(ZoneId.systemDefault()).toLocalTime().toString()

            itemView.setOnClickListener {
                itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(rawHtml.address)))
            }

            itemView.setOnLongClickListener(onLongClickListener(rawHtml.address))
        }
    }

    override fun getItemCount(): Int {
        Log.i(javaClass.name, "Size requested: ${values.size}")
        return values.size
    }

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