package com.example.freshcard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.freshcard.Structure.ResultTest
import com.example.freshcard.databinding.ActivityShowResultBinding

class ShowResultActivity : AppCompatActivity() {
    private lateinit var result: ResultTest
    private lateinit var binding: ActivityShowResultBinding
    private var totalItems: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        result = intent.getSerializableExtra("result") as ResultTest
        totalItems = intent.getIntExtra("totalItems", 0)
        if(result!=null) {
            setUpResultView()
        }

        binding.btnBack.setOnClickListener{
            var intent = Intent()
            intent.putExtra("isAgain", false)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnAgain.setOnClickListener{
            var intent = Intent()
            intent.putExtra("isAgain", true)
            setResult(RESULT_OK, intent)
            finish()
        }

    }

    private fun setUpResultView(){
        binding.txtResultPercent.text = "${result.amountCorrect*100 /totalItems}%"
        binding.txtResult.text = "${result.amountCorrect}/${totalItems}"
        binding.txtTime.text = secToTime(result.duration)
        binding.progress.progress = result.amountCorrect*100 /totalItems
    }

    private fun secToTime(sec: Int): String {
        val time = when {
            sec < 10 -> "00:0$sec"
            sec < 60 -> "00:$sec"
            sec < 600 -> if (sec % 60 < 10) "0${sec / 60}:0${sec % 60}" else "0${sec / 60}:${sec % 60}"
            else -> "${sec / 600}:${sec % 600}"
        }

        return time
    }
}