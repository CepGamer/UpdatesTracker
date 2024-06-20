package com.cepgamer.updatestracker.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.VERTICAL
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

    lateinit var updater: RawHtmlUpdater

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
                        adapter = DefaultHtmlPageRecyclerViewAdapter(list, this@HtmlPageFragment)
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

        binding.refreshCircle.setOnRefreshListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    updater.updateHtmls()
                }
                binding.refreshCircle.isRefreshing = false
            }
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