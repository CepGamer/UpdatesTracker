package com.cepgamer.updatestracker

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.cepgamer.updatestracker.databinding.HtmlPageFragmentItemBinding
import com.cepgamer.updatestracker.model.RawHtmlEntity
import com.cepgamer.updatestracker.ui.main.HtmlPageFragment
import java.time.ZoneId

class DefaultHtmlPageRecyclerViewAdapter(
    private val values: List<RawHtmlEntity>,
    private val fragment: HtmlPageFragment,
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
        val appEntry = values[position]
        holder.apply {
            addressTextView.text = appEntry.address
            lastUpdatedTextView.text =
                appEntry.lastUpdate.atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
            lastCheckedTextView.text =
                appEntry.lastCheck.atZone(ZoneId.systemDefault()).toLocalDateTime().toString()

            itemView.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(appEntry.address)
                    )
                )
            }

            editButton.setOnClickListener {
                composer.setContent { GenerateDropdown(it, composer, appEntry.address) }
            }
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
        val editButton = binding.editEntryButton
        val composer = binding.composeView

        override fun toString(): String {
            return "${super.toString()}\n$addressTextView"
        }
    }

    @Composable
    private fun GenerateDropdown(it: View, container: ComposeView, address: String) {
        if (true) {
            DropdownMenu(expanded = true, onDismissRequest = {
                container.setContent { }
            }) {
                DropdownMenuItem(
                    text = { Text(fragment.getString(R.string.edit_title_text)) },
                    onClick = { /*TODO*/ })
                DropdownMenuItem(
                    text = { Text(fragment.getString(R.string.delete_text)) },
                    onClick = {
                        AlertDialog.Builder(it.context)
                            .setTitle("Are you sure you want to delete this entry?")
                            .setPositiveButton(R.string.yes) { _, _ ->
                                fragment.updater.deleteHtml(address)
                            }
                            .setNegativeButton(R.string.no) { _, _ -> }
                            .show()
                        container.setContent { }
                    })
                DropdownMenuItem(
                    text = { Text(fragment.getString(R.string.change_position)) },
                    onClick = { /*TODO*/ })
            }
        } else {
            AlertDialog.Builder(it.context)
                .setTitle("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    fragment.updater.deleteHtml(address)
                }
                .show()
        }
    }
}