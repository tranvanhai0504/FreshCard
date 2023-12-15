package com.example.freshcard.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.freshcard.R
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.SearchActivity
import com.example.freshcard.adapters.HomeAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var tabLayout : TabLayout
    lateinit var viewPager: ViewPager2
    lateinit var searchInput : EditText
    lateinit var btnSearch : AppCompatImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = this.requireView().findViewById(R.id.tabLayout)
        viewPager = this.requireView().findViewById(R.id.pager)
        viewPager.isSaveEnabled = false

        searchInput = this.requireView().findViewById(R.id.editText)
        btnSearch = this.requireView().findViewById(R.id.btnSearch)

        btnSearch.setOnClickListener {
            var inputTxt = searchInput.text.toString()

            if(inputTxt.isNotEmpty()){
                var intent = Intent(this.requireContext(), SearchActivity::class.java)

                intent.putExtra("textSearch", inputTxt)
                startActivity(intent)
            }
        }

        val viewAdapter = HomeAdapter(this)
        viewPager.adapter = viewAdapter

        TabLayoutMediator(tabLayout, viewPager){ tab, positon ->
            if(positon == 0){
                tab.text = "New"
            }else{
                tab.text = "Process"
            }
        }.attach()
    }
}