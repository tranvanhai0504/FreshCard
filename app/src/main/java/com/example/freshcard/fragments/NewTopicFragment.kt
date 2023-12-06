package com.example.freshcard.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.R
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.adapters.TopicNewHomeAdapter

class NewTopicFragment : Fragment() {
    lateinit var newTopicAdapter : TopicNewHomeAdapter
    var newTopicList : ArrayList<Topic> = ArrayList<Topic>()
    lateinit var cardRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_topic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cardRecyclerView = this.requireView().findViewById(R.id.listNewTopicView)
        cardRecyclerView.layoutManager = LinearLayoutManager(context)
        newTopicList.add(Topic("id","-Nk-5J5lT1gXrDL1NAdQ", "Animals", ArrayList<TopicItem>(), true, ArrayList<String>()))

        Log.i("listTopic", newTopicList.toString())
        newTopicAdapter = TopicNewHomeAdapter(newTopicList, this)
        cardRecyclerView.adapter = newTopicAdapter
    }
}