package com.example.freshcard.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.freshcard.fragments.HomeFragment
import com.example.freshcard.fragments.NewTopicFragment
import com.example.freshcard.fragments.ProcessTopicFragment

class HomeAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> {
                NewTopicFragment()
            }

            1-> {
                ProcessTopicFragment()
            }

            else -> {
                NewTopicFragment()
            }
        }
    }


}