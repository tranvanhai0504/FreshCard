package com.example.freshcard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.adapters.CardResultAdapter

class ResutlDetailActivity : AppCompatActivity() {
    lateinit var adapter : CardResultAdapter
    lateinit var recyclerView: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resutl_detail)

        var listResult = (intent.getSerializableExtra("list") as? ArrayList<ArrayList<String>>)!!

        var backButton = findViewById<AppCompatButton>(R.id.btn_back_detail)
        recyclerView = findViewById(R.id.recyclerViewList)

        backButton.setOnClickListener {
            finish()
        }

        adapter = CardResultAdapter(listResult)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}