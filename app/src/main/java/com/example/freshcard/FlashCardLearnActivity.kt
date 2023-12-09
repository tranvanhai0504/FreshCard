package com.example.freshcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.databinding.ActivityFlashCardLearnBinding

class FlashCardLearnActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFlashCardLearnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFlashCardLearnBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //get id topic
        var id = intent.getStringExtra("idTopic")

        //get id user
        val sharedPreferences = this.applicationContext?.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        var userId = sharedPreferences?.getString("idUser", "undefined")!!

        //create learning topic
        if (id != null) {
            UserDAO().addLearningTopic(userId, id)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }


}