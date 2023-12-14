package com.example.freshcard

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.TestResultDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.RankingItemView
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.UserRankingItem
import com.example.freshcard.adapters.UserRankingAdapter

class RankingDetailActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtTopicname: TextView
    private lateinit var btnBack: androidx.appcompat.widget.AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking_detail)
        recyclerView = findViewById(R.id.rankingInfoRecyclerView)
        txtTopicname = findViewById(R.id.txtTopicName)
        btnBack = findViewById(R.id.btn_back)
        recyclerView.layoutManager = LinearLayoutManager(this)
        var adapterData =  ArrayList<UserRankingItem>()
        var adapter = UserRankingAdapter(adapterData, this)
        recyclerView.adapter = adapter
        var topicId = intent.getStringExtra("topicId")
        btnBack.setOnClickListener{
            finish()
        }
        TestResultDAO().getRankingByTopic(topicId!!) {
            listResults->
                TopicDAO().getName(topicId) {
                    txtTopicname.text = "Topic: $it"
                }
                for(item in listResults) {
                UserDAO().getName(item.idUser) {name->
                    var performent = "${item.amountCorrect} correct / ${secToTime(item.duration)}"
                    adapter.addItem(UserRankingItem(name, performent))
                }
            }
        }

    }
    fun secToTime(i: Int): String {
        val time = when {
            i < 10 -> "00:0$i"
            i < 60 -> "00:$i"
            i < 600 -> if (i % 60 < 10) "0${i / 60}:0${i % 60}" else "0${i / 60}:${i % 60}"
            else -> "${i / 600}:${i % 600}"
        }
        return time
    }
}