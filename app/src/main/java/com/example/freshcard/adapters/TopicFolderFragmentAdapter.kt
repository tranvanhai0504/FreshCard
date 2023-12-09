package com.example.freshcard.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.freshcard.fragments.FoldersFragment
import com.example.freshcard.fragments.TopicsFragment

class TopicFolderFragmentAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment)  {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> {
                return FoldersFragment()
            }
            1-> {
                return TopicsFragment()
            }
            else -> {
                return FoldersFragment()
            }
        }
    }

}