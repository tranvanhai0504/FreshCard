package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.freshcard.databinding.ActivityResultPointsBinding

class ResultPointsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultPointsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultPointsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textEndQues = intent.getStringExtra("textEndQues")
        val amountCorrect = intent.getIntExtra("amountCorrect", 0)
        val duration = intent.getIntExtra("duration", 0) // corrected duplicate key
        val scorePlus = intent.getIntExtra("scorePlus", 0)
        val durationMillis = duration.toLong()
        binding.resultPercent.text = "$scorePlus points"
        binding.timeResult.text = formatDuration(durationMillis)
        binding.resultCorrect.text = "$amountCorrect / $textEndQues"

        binding.btnBack.setOnClickListener{
            finish()
        }
        binding.btnDoAgain.setOnClickListener {
            val intent = Intent(this, FlashCardLearnActivity::class.java)
            finish()
            startActivity(intent)
        }
        binding.btnBackMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
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