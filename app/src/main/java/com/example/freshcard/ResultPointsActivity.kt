package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.Structure.ResultTest
import com.example.freshcard.databinding.ActivityResultPointsBinding

class ResultPointsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultPointsBinding
    private  lateinit var testData: ArrayList<ArrayList<String>>
    private lateinit var result: ResultTest
    private var totalItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testData = (intent.getSerializableExtra("testResult") as? ArrayList<ArrayList<String>>)!!
        result = intent.getSerializableExtra("result") as ResultTest
        totalItems = intent.getStringExtra("textEndQues")?.toInt()!!

        val textEndQues = intent.getStringExtra("textEndQues")
        val amountCorrect = intent.getIntExtra("amountCorrect", 0)
        val duration = intent.getIntExtra("duration", 0) // corrected duplicate key
        val scorePlus = intent.getIntExtra("scorePlus", 0)
        val durationMillis = duration.toLong()

        val idTopic = intent.getStringExtra("idTopic")

        binding.resultPercent.text = "$scorePlus points"
        binding.timeResult.text = formatDuration(durationMillis)
        binding.resultCorrect.text = "$amountCorrect / $textEndQues"

        binding.btnBack.setOnClickListener{
            finish()
        }
        binding.btnDoAgain.setOnClickListener {
            val intent = Intent(this, ChooseTypeActivity::class.java)
            intent.putExtra("idTopicTest", idTopic)
            finish()
            startActivity(intent)
        }
        binding.btnBackMain.setOnClickListener {
            finish()
        }

        binding.btnViewDetail.setOnClickListener {
            val intent = Intent(this, ResutlDetailActivity::class.java)
            intent.putExtra("list", testData)
            startActivity(intent)
        }

    }

    private fun formatDuration(millis: Long): String {
        val seconds = millis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}