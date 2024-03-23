package com.cepgamer.updatestracker.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cepgamer.updatestracker.DefaultHtmlPageRecyclerViewAdapter
import com.cepgamer.updatestracker.databinding.HtmlPageFragmentItemListBinding
import com.cepgamer.updatestracker.model.RawHtmlUpdater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A fragment representing a list of Items.
 */
class HtmlPageFragment(viewModelLazy: Lazy<RawHtmlViewModel>) : Fragment() {

    private val viewModel: RawHtmlViewModel by viewModelLazy
    private lateinit var binding: HtmlPageFragmentItemListBinding
    private lateinit var updater: RawHtmlUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updater = RawHtmlUpdater(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HtmlPageFragmentItemListBinding.inflate(layoutInflater)
        val view = binding.root

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rawHtmlFlow.collect { list ->
                    Log.i(javaClass.name, "collecting htmls: ${list.joinToString { it.address }}.")
                    // Set the adapter
                    with(binding.htmlList) {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DefaultHtmlPageRecyclerViewAdapter(list) { address ->
                            OnLongClickListener {
                                AlertDialog.Builder(it.context)
                                    .setTitle("Are you sure you want to delete this entry?")
                                    .setPositiveButton(android.R.string.yes) { _, _ ->
                                        updater.deleteHtml(address)
                                    }
                                    .show()

                                true
                            }
                        }
                        addItemDecoration(DividerItemDecoration(context, VERTICAL))
                    }
                }
            }

            withContext(Dispatchers.IO) {
                updater.updateHtmls()
            }
        }

        binding.addHtmlAddress.setOnClickListener {
            startAddressMonitoring(binding.htmlAddressEditText.text.toString())
        }

        return view
    }

    private fun startAddressMonitoring(address: String) {
        Log.i(javaClass.name, "Adding address, starting monitoring.")
        updater.insertHtmlByAddress(address)
    }

    companion object {
        fun newInstance(viewModel: Lazy<RawHtmlViewModel>) = HtmlPageFragment(viewModel)
    }
}