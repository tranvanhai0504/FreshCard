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
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.AddTopic
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.adapters.FolderItemAdapter
import com.example.freshcard.adapters.TopicAdapter

class TopicsFragment : Fragment() {
    private lateinit var topicsAdapter: TopicAdapter
    private lateinit var topicRecyclerView: RecyclerView
    private  lateinit var btnNewTopic: Button
    private var mlist = ArrayList<TopicInfoView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var adapterData = ArrayList<TopicInfoView>()
        val sharedPreferences = requireContext().getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", "undefined")!!
        topicRecyclerView = this.requireView().findViewById(R.id.topicsRecyclerView)
        topicsAdapter = TopicAdapter(mlist, requireContext(), "view")
        topicRecyclerView.layoutManager = LinearLayoutManager(context)
        TopicDAO().getTopicInfoViewByOwner(userId) {data ->
            if(mlist.equals(data)){
                Log.i("taf", data.toString())
            }

            mlist = data
            topicsAdapter.setList(mlist)
            Log.i("taggg", mlist.size.toString())
            topicsAdapter.notifyDataSetChanged()
        }
        topicRecyclerView.adapter = topicsAdapter
        btnNewTopic = this.requireView().findViewById(R.id.btnNewTopic)
        btnNewTopic.setOnClickListener{
            v->
            var intent = Intent(context, AddTopic::class.java)
            intent.putExtra("edit", "false")
            startActivity(intent)
        }
    }

    fun updateAdapter(data: ArrayList<TopicInfoView>) {
        Log.e("topic-", "${data}")
        topicsAdapter = TopicAdapter(data, requireContext(), "view")
        topicRecyclerView.adapter = topicsAdapter
    }

    fun reloadTopicList() {
        var userId = UserDAO().getUserIdShareRef(requireContext())
        TopicDAO().getTopicInfoViewByOwner(userId) {data -> updateAdapter(data)}
    }
}