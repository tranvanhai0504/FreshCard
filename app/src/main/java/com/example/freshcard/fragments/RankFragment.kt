package com.example.freshcard.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.TestResultDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.RankingItemView
import com.example.freshcard.Structure.ResultTest
import com.example.freshcard.Structure.User
import com.example.freshcard.adapters.RankingAdapter
import com.example.freshcard.adapters.TopicFolderFragmentAdapter
import com.example.freshcard.databinding.FragmentRankBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 * Use the [RankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RankFragment : Fragment(R.layout.fragment_rank) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerView: RecyclerView
    private var adapterData: ArrayList<RankingItemView> = ArrayList(emptyList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_rank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = this.requireView().findViewById(R.id.rankingRecyclerview)
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        var adapter = RankingAdapter(adapterData,this.requireContext())
        recyclerView.adapter = adapter
        TestResultDAO().getTestResult() {
            hashmap->
            adapterData = ArrayList(emptyList<RankingItemView>())
            for(data in hashmap) {
                var testResults = data.value
                Log.e("result", "${testResults.size}")
                if(testResults.size>=3) {
                    TopicDAO().getTopicById(testResults.get(0).idTopic) {topic->
                        UserDAO().getUserById(testResults.get(0).idUser) {user1->
                            UserDAO().getUserById(testResults.get(1).idUser) {
                                user2->
                                UserDAO().getUserById(testResults.get(2).idUser) {
                                        user3->
                                        adapter.addItem(RankingItemView(testResults.get(0).idTopic,topic.title, topic.owner, user1!!,user2!!,user3!!))
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RankFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RankFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
}