package com.groodysoft.tdaexample.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.groodysoft.tdaexample.R

open class SubtitledFragment : Fragment() {

//    var toolbarSubtitle = ""
//        set(value) {
//
//            setSubtitle(value)
//            field = value
//        }

    var toolbarTitle = ""
        set(value) {

            setTitle(value)
            field = value
        }

//    private fun setSubtitle(subtitle: String) {
//        val actionBar = (activity as AppCompatActivity).supportActionBar!!
//        actionBar.subtitle = subtitle
//    }

    private fun setTitle(title: String) {
        val actionBar = (activity as AppCompatActivity).supportActionBar!!
        actionBar.title = title

        val isMainFragment = title == getString(R.string.app_name)
        actionBar.setDisplayHomeAsUpEnabled(!isMainFragment)
        actionBar.setDisplayShowHomeEnabled(!isMainFragment)
    }
}