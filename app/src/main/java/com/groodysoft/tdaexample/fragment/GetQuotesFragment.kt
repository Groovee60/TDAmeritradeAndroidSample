package com.groodysoft.tdaexample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groodysoft.tdaexample.R
import com.groodysoft.tdaexample.adapter.QuoteAdapter
import com.groodysoft.tdaexample.databinding.FragmentQuotesBinding
import com.groodysoft.tdaexample.viewmodel.TDAQuoteViewModel

class GetQuotesFragment : SubtitledFragment() {

    private lateinit var binding: FragmentQuotesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQuotesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle = getString(R.string.get_quotes)

        val quoteViewModel = TDAQuoteViewModel("/ES,NFLX,AAPL,GOOG,TSLA")
        quoteViewModel.quotes.observe(viewLifecycleOwner, {

            val adapter = QuoteAdapter()
            binding.quoteRecycler.adapter = adapter
            adapter.submitList(it)
        })
    }
}
