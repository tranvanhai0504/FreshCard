package com.example.freshcard

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
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

        binding.txtMessage2.setOnClickListener{
            setRankMessage(21)
        }

    }

    private fun setUpResultView(){
        setRankMessage(1)
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

    private fun setRankMessage(rank: Int) {
        var message = "You have achieved #$rank position on dashboard"
        val spannable = SpannableString(message)
        val greenColor = ContextCompat.getColor(this, R.color.mediumGreen)
        val rankPositionStart = message.indexOf("#$rank")
        val rankPositionEnd = rankPositionStart + rank.toString().length+1
        spannable.setSpan(
            ForegroundColorSpan(greenColor),
            rankPositionStart,
            rankPositionEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.txtMessage2.text = spannable
    }
}