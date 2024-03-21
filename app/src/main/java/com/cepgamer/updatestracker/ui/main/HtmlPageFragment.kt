package com.cepgamer.updatestracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cepgamer.updatestracker.DefaultHtmlPageRecyclerViewAdapter
import com.cepgamer.updatestracker.databinding.HtmlPageFragmentItemListBinding
import com.cepgamer.updatestracker.model.RawHtmlUpdater
import kotlinx.coroutines.launch

/**
 * A fragment representing a list of Items.
 */
class HtmlPageFragment : Fragment() {

    private lateinit var viewModel: RawHtmlViewModel
    private lateinit var binding: HtmlPageFragmentItemListBinding
    private lateinit var updater: RawHtmlUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[RawHtmlViewModel::class.java]

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
                    // Set the adapter
                    with(binding.htmlList) {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DefaultHtmlPageRecyclerViewAdapter(list)
                    }
                }
            }
        }

        binding.addHtmlAddress.setOnClickListener {
            startAddressMonitoring(binding.htmlAddressEditText.text.toString())
        }

        return view
    }

    private fun startAddressMonitoring(address: String) {
        updater.insertHtmlByAddress(address)
    }

    companion object {
        fun newInstance() = HtmlPageFragment()
    }
}