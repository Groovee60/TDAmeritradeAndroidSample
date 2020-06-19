package com.groodysoft.tdaexample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.groodysoft.tdaexample.R
import com.groodysoft.tdaexample.adapter.QuoteAdapter
import com.groodysoft.tdaexample.viewmodel.TDAQuoteViewModel
import kotlinx.android.synthetic.main.fragment_quotes.*

class GetQuotesFragment : SubtitledFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quotes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarTitle = getString(R.string.get_quotes)

        val quoteViewModel = TDAQuoteViewModel("/ES,NFLX,AAPL,GOOG,TSLA")
        quoteViewModel.quotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            val adapter = QuoteAdapter()
            quoteRecycler.adapter = adapter
            adapter.submitList(it)
        })
    }
}
