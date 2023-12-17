package com.example.freshcard

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.freshcard.Structure.Topic
import com.example.freshcard.databinding.ActivityChooseTypeBinding

class ChooseTypeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseTypeBinding
    private var isBtnVntoengSelected = false
    private var isBtnEngtovnSelected = false
    private lateinit var topic: Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var testType = intent.getStringExtra("testType")
        if(testType == "multipleChoices") {
            topic = (intent.getSerializableExtra("topic") as? Topic)!!
        }
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
                if(testType == "multipleChoices"){
                    var intent = Intent(this, MultipleChoicesTestActivity::class.java)
                    intent.putExtra("topic", topic)
                    intent.putExtra("selectedButton", "btnVntoeng")
                    startActivityForResult(intent, 100)
                }else {
                    val intent = Intent(this, WordTypingActivity::class.java)
                    intent.putExtra("selectedButton", "btnVntoeng") // Gửi thông điệp về nút được chọn
                    intent.putExtra("idTopicTest", idTopic)
                    startActivity(intent)
                }
            }else if (isBtnEngtovnSelected){
                if(testType == "multipleChoices"){
                    val intent = Intent(this, MultipleChoicesTestActivity::class.java)
                    intent.putExtra("selectedButton", "btnEngtovn")
                    intent.putExtra("topic", topic)
                    startActivityForResult(intent, 100)
                }
                else {
                    val intent = Intent(this, WordTypingActivity::class.java)
                    intent.putExtra("selectedButton", "btnEngtovn") // Gửi thông điệp về nút được chọn
                    intent.putExtra("idTopicTest", idTopic)
                    startActivity(intent)
                }
            }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100 && data!!.getBooleanExtra("isFinish", false)) {
            var intent = Intent()
            intent.putExtra("isFinish", true)
            setResult(100, intent)
            finish()
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
