package com.example.freshcard.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.AddTopic
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.adapters.TopicAdapter


class ProcessTopicFragment : Fragment() {

    private lateinit var topicsAdapter: TopicAdapter
    private lateinit var topicRecyclerView: RecyclerView
    private var mlist = ArrayList<TopicInfoView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_process_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", "undefined")!!
        topicRecyclerView = this.requireView().findViewById(R.id.listProcessTopicView)
        topicsAdapter = TopicAdapter(mlist, requireContext(), "view")
        topicRecyclerView.layoutManager = LinearLayoutManager(context)
        TopicDAO().getTopicLearningByUser() { data ->
            mlist = data
            topicsAdapter.setList(mlist)
            topicsAdapter.notifyDataSetChanged()
        }
        topicRecyclerView.adapter = topicsAdapter
    }

    fun updateAdapter(data: ArrayList<TopicInfoView>) {
        topicsAdapter = TopicAdapter(data, requireContext(), "view")
        topicRecyclerView.adapter = topicsAdapter
    }

    fun reloadTopicList() {
        TopicDAO().getTopicLearningByUser() { data -> updateAdapter(data)}
    }


}