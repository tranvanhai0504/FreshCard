package com.example.freshcard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.freshcard.R
import com.example.freshcard.adapters.TopicFolderFragmentAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class FolderFragment : Fragment(R.layout.fragment_folder) {
    private lateinit var tabLayout : TabLayout
     private lateinit var viewPager: ViewPager2
    private lateinit var fragmentAdapter : TopicFolderFragmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//
//        }
//
//        tabLayout!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                tab?.let {
//                    viewPager!!.currentItem = it.position
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {}
//            override fun onTabReselected(tab: TabLayout.Tab?) {}
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return  inflater.inflate(R.layout.fragment_folder, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = this.requireView().findViewById(R.id.tabLayout)
        viewPager = this.requireView().findViewById(R.id.pager)
        viewPager.isSaveEnabled = false

        fragmentAdapter = TopicFolderFragmentAdapter(this)
        viewPager.adapter = fragmentAdapter


        TabLayoutMediator(tabLayout, viewPager){ tab, positon ->
            if(positon == 0){
                tab.text = "Folders"
            }else{
                tab.text = "Topics"
            }
        }.attach()
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FolderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FolderFragment().apply {

            }
    }
}