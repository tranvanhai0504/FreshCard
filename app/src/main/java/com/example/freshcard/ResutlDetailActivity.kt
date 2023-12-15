package com.example.freshcard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
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
        var amountCorrect = 0
        var amountincorrect = 0

        for(item in listResult){
            if(item[1] == item[2]){
                amountCorrect++
            }else{
                amountincorrect++
            }
        }

        var txtCorrect = findViewById<TextView>(R.id.textView12)
        var txtIncorrect = findViewById<TextView>(R.id.textView13)

        txtCorrect.text = "Total correct: $amountCorrect"
        txtIncorrect.text = "Total incorrect: $amountincorrect"

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