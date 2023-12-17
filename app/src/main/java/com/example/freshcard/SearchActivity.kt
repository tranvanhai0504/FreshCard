package com.example.freshcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.Structure.Topic
import com.example.freshcard.adapters.TopicNewHomeAdapter
import com.example.freshcard.adapters.TopicSearchAdapter
import com.example.freshcard.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding
    private var listTopic : ArrayList<Topic> = ArrayList<Topic>()
    private lateinit var adapter : TopicSearchAdapter
    private lateinit var recyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        updateUi()

        //get text search
        var textSearch = intent.getStringExtra("textSearch")

        binding.editSearch.setText(textSearch)

        if (textSearch != null) {
            getListTopicByFilter(textSearch)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSearch2.setOnClickListener {
            var searchText = binding.editSearch.text.toString()
            getListTopicByFilter(searchText)
        }
    }

    private fun getListTopicByFilter(text : String){
        TopicDAO().getTopicBySearchText(text){
            listTopic = it
            updateList(listTopic = listTopic)
        }
    }

    private fun updateUi(){
        adapter = TopicSearchAdapter(listTopic, this)

        recyclerView = findViewById(R.id.listNewTopicViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun updateList(listTopic : ArrayList<Topic>){
        adapter.mList = listTopic
        adapter.notifyDataSetChanged()
    }
}