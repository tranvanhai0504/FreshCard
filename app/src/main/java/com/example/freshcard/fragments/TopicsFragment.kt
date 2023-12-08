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


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TopicsFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var topicsAdapter: TopicAdapter
    private lateinit var topicRecyclerView: RecyclerView
    private  lateinit var btnNewTopic: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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
        topicRecyclerView.layoutManager = LinearLayoutManager(context)
        TopicDAO().getTopicInfoViewByOwner(userId) {data -> updateAdapter(data)}
        btnNewTopic = this.requireView().findViewById(R.id.btnNewTopic)
        btnNewTopic.setOnClickListener{
            v->
            startActivity(Intent(context, AddTopic::class.java))
        }
    }

    fun updateAdapter(data: ArrayList<TopicInfoView>) {
        Log.e("topic-", "${data}")
        topicsAdapter = TopicAdapter(data, requireContext())
        topicRecyclerView.adapter = topicsAdapter
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TopicsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TopicsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}