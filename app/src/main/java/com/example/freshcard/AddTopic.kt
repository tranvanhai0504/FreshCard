package com.example.freshcard

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.freshcard.Adapter.CardAdapter
import com.example.freshcard.Structure.TopicItem

class AddTopic : AppCompatActivity() {
    private lateinit var cardsRectyclerView: RecyclerView
    private lateinit var btnEditName: ImageButton
    private lateinit var inputTopicName: EditText
    private lateinit var btnBackToHome: ImageButton
    private lateinit var btnSaveTopicName: ImageButton
    private var adapterData =  ArrayList<TopicItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)
        cardsRectyclerView = findViewById(R.id.cardsReccylerView)
        btnEditName = findViewById(R.id.btnEditTopicName)
        inputTopicName = findViewById(R.id.inputTopicName)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnSaveTopicName = findViewById(R.id.btnSaveTopicName)
        btnSaveTopicName.isVisible = false
        btnBackToHome.setOnClickListener{
            v->
            finish()
        }
        btnSaveTopicName.setOnClickListener{
            v->
            handleSaveName()
        }
        btnEditName.setOnClickListener {
            handleClickEditName()
        }
        cardsRectyclerView.layoutManager = LinearLayoutManager(this)

        adapterData.add(TopicItem("Melon", "Qua Dua", "Some fruit have water inside", "dlasn"))
        adapterData.add(TopicItem("Strawberry", "Qua Dau", "Some fruit with red skin", "dlasn"))
        adapterData.add(TopicItem("Orange", "Qua Cam", "Some fruit have to much vitamin C", "dlasn"))
        adapterData.add(TopicItem("Orange", "Qua Cam", "Some fruit have to much vitamin C", "dlasn"))
        adapterData.add(TopicItem("Orange", "Qua Cam", "Some fruit have to much vitamin C", "dlasn"))
        adapterData.add(TopicItem("Strawberry", "Qua Dau", "Some fruit with red skin", "dlasn"))

        cardsRectyclerView.adapter =CardAdapter(adapterData, this)

    }

    fun handleClickEditName() {
        inputTopicName.isEnabled = true
        btnEditName.isVisible = false
        btnSaveTopicName.isVisible = true
        inputTopicName.requestFocus()

    }

    fun handleSaveName() {
        inputTopicName.isEnabled = false
        btnEditName.isVisible = true
        btnSaveTopicName.isVisible = false
        inputTopicName.clearFocus()
    }
}