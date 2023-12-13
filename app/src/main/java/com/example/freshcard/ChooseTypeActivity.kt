package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.freshcard.databinding.ActivityChooseTypeBinding

class ChooseTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseTypeBinding
    private var isBtnVntoengSelected = false
    private var isBtnEngtovnSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var idTopic = intent.getStringExtra("idTopicTest")
        binding = ActivityChooseTypeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEngtovn.setOnClickListener {
            isBtnEngtovnSelected = true
            isBtnVntoengSelected = false

            binding.btnEngtovn.backgroundTintList = resources.getColorStateList(R.color.mediumGreen)
            binding.btnEngtovn.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.btnVntoeng.backgroundTintList = resources.getColorStateList(R.color.white)
            binding.btnVntoeng.setTextColor(ContextCompat.getColor(this, R.color.mediumGreen))
            updateSubmitButtonState()
        }

        binding.btnVntoeng.setOnClickListener {
            isBtnVntoengSelected = true
            isBtnEngtovnSelected = false

            binding.btnVntoeng.backgroundTintList = resources.getColorStateList(R.color.mediumGreen)
            binding.btnVntoeng.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.btnEngtovn.backgroundTintList = resources.getColorStateList(R.color.white)
            binding.btnEngtovn.setTextColor(ContextCompat.getColor(this, R.color.mediumGreen))

            updateSubmitButtonState()
        }

        binding.btnSubmit.isEnabled = false
        updateSubmitButtonState()

        binding.btnSubmit.setOnClickListener {
            if(isBtnVntoengSelected){
                val intent = Intent(this, WordTypingActivity::class.java)
                intent.putExtra("selectedButton", "btnVntoeng") // Gửi thông điệp về nút được chọn
                intent.putExtra("idTopicTest", idTopic)
                startActivity(intent)
            }else if (isBtnEngtovnSelected){
                val intent = Intent(this, WordTypingActivity::class.java)
                intent.putExtra("selectedButton", "btnEngtovn") // Gửi thông điệp về nút được chọn
                intent.putExtra("idTopicTest", idTopic)
                startActivity(intent)
            }
        }
    }

    private fun updateSubmitButtonState() {
        if (isBtnVntoengSelected || isBtnEngtovnSelected) {
            // Kích hoạt btn_submit và đặt màu nền cho nó
            binding.btnSubmit.isEnabled = true
            binding.btnSubmit.backgroundTintList = resources.getColorStateList(R.color.mediumGreen)
        } else {
            // Vô hiệu hóa btn_submit và đặt màu nền về mặc định
            binding.btnSubmit.isEnabled = false
            binding.btnSubmit.backgroundTintList = resources.getColorStateList(R.color.grayDefault)
        }
    }
}
