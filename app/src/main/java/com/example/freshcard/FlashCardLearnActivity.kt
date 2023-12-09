package com.example.freshcard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.adapters.TopicAdapter
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

        binding.btnOption.setOnClickListener{
            showPopupMenu(id!!)
        }
    }

    private fun showPopupMenu(id: String) {
        val popupMenu = PopupMenu(this, binding.btnOption)
        popupMenu.inflate(R.menu.learing_popup_menu) // Inflate your menu resource
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.learningPopupmenuEdit -> {
                    var intent = Intent(this, AddTopic::class.java)
                    intent.putExtra("edit", id)
                    startActivity(intent)
                    true
                }

                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }


}