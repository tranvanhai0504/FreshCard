package com.example.freshcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.ResultTestDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.databinding.ActivityWordTypingBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WordTypingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordTypingBinding
    private var startTimeMillis: Long = 0
    private val handler = Handler()
    private val topicDAO = TopicDAO()
    var currentItemIndex = 0
    var checkcurrentItemIndex = 0
    var scorePlus = 0
    var duration = 0
    var amountCorrect = 0
    var textEndQues: String? = null
    var textFirstQues:String? = null
    var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val idUser = sharedPreferences.getString("idUser", null)
        id = idUser
        getTopic(currentItemIndex)

        binding = ActivityWordTypingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startTimeMillis = System.currentTimeMillis()

        initializeViews()
        startElapsedTimeUpdate()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnBackHome.setOnClickListener {
            handler.removeCallbacksAndMessages(null) // Remove callbacks to stop the update
            val intent = Intent(this, FlashCardLearnActivity::class.java)
            startActivity(intent)
        }


        binding.score.text = "0"

    }

    private fun getTopic(count: Int){
        val selectedButton = intent.getStringExtra("selectedButton")
        val idTopic = intent.getStringExtra("idTopicTest")

        topicDAO.getTopicById(idTopic!!) { retrievedTopic ->
            runOnUiThread {
                val items = retrievedTopic.items

                if (items != null && items.isNotEmpty()) {

                    binding.textFirstQues.text = (items.indexOf(items.first()) + 1 + count).toString()
                    binding.textEndQues.text = (items.indexOf(items.last()) + 1).toString()
                    val ctextEndQues = (items.indexOf(items.last()) + 1).toString()
                    val ctextFirstQues = (items.indexOf(items.first()) + 1 + count).toString()
                    textFirstQues = ctextFirstQues
                    textEndQues = ctextEndQues

                    // Cập nhật ProgressBar dựa trên tỉ lệ textFirstQues / textEndQues
                    updateProgressBar(items.size, count)

                    if (items.size >= 1) {
                        val firstItem = items[count]
                        val itemEn = firstItem.en
                        val itemVie = firstItem.vie
                        val itemDescription = firstItem.description
                        val itemImage = firstItem.image
                        // Sử dụng giá trị của các trường ở đây, ví dụ:
                        if(selectedButton == "btnEngtovn"){
                            engToVN(itemEn, itemVie, itemImage)
                        }else if (selectedButton == "btnVntoeng"){
                            vnToEng(itemVie, itemEn, itemImage)
                        }
                        checkcurrentItemIndex = items.indexOf(items.last())
                        binding.resultname.text = null
                    }
                }
            }
        }
    }
    private fun initializeViews() {
        updateElapsedTime()
    }

    private fun startElapsedTimeUpdate() {
        handler.post(object : Runnable {
            override fun run() {
                updateElapsedTime()
                handler.postDelayed(this, 1000) // Update every 1 second
            }
        })
    }

    private fun updateElapsedTime() {
        val elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis
        val elapsedTimeInt = elapsedTimeMillis.toInt()
        duration = elapsedTimeInt
        binding.timeResult.text = formatTime(elapsedTimeMillis)
    }

    private fun formatTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Thêm phương thức updateProgressBar
    private fun updateProgressBar(totalQuestions: Int, currentQuestionIndex: Int) {
        val progressBar = binding.progressBar
        val progress = ((currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat() * 100).toInt()
        progressBar.progress = progress
    }

    private fun engToVN(desEng: String, desVn: String, img: String){
        binding.nameTopicItem.text = desEng.toString()
        val cdesVn= desVn.trim()

        if (img != null){
            ImageDAO().getImage(img.toString(), binding.imageView4, "images")
        }
        binding.btnSubmit.setOnClickListener {
            val enterKey = binding.editResult.text.toString().trim()

            if (enterKey.equals(cdesVn, ignoreCase = true)) {
                // Bước 1: Kiểm tra enterKey có trùng với cdesEng không bất kể chữ in hoa.
                binding.resultname.text = "Exactly: $desVn"
                scorePlus += 30
                amountCorrect++
                binding.score.text = scorePlus.toString()
                binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.strongGreen))
                binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                // Bước 2: Nếu enterKey trùng với cdesEng, thì btnSubmit có text là "Next".

                binding.btnSubmit.setOnClickListener {
                    if (binding.btnSubmit.text == "Next") {
                        // Bước 3: Nếu btnSubmit có text là "Next", thì thực hiện currentItemIndex++; getTopic(currentItemIndex).
                        currentItemIndex++
                        getTopic(currentItemIndex)
                        binding.btnSubmit.text = "Check"
                        binding.editResult.text = null

                        // Sau đó, btnSubmit sẽ có text là "Check".
                    }else if (binding.btnSubmit.text == "Finish") {
                        // Bước 4: Nếu btnSubmit có text là "Finish", chuyển sang activity mới.
                        performActivityTransfer()
                    }
                }
            } else {
                // Bước 1: Kiểm tra enterKey không trùng với cdesEng không bất kể chữ in hoa.
                binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                binding.resultname.text = "Correct answer: $desVn"
                scorePlus += 0
                binding.score.text = scorePlus.toString()
                binding.editResult.isEnabled = false

                binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.warningRed))


                // Bước 2: Nếu enterKey không trùng với cdesEng, thì btnSubmit có text là "Next".

                binding.btnSubmit.setOnClickListener {
                    if (binding.btnSubmit.text == "Next") {
                        binding.editResult.isEnabled = true
                        binding.editResult.text = null
                        // Bước 3: Nếu btnSubmit có text là "Next", thì thực hiện currentItemIndex++; getTopic(currentItemIndex).
                        currentItemIndex++
                        getTopic(currentItemIndex)
                        binding.btnSubmit.text = "Check"
                        // Sau đó, btnSubmit sẽ có text là "Check".
                    }else if (binding.btnSubmit.text == "Finish") {
                        // Bước 4: Nếu btnSubmit có text là "Finish", chuyển sang activity mới.
                        performActivityTransfer()
                    }
                }
            }
        }
    }

    private fun vnToEng(desVn: String, desEng: String, img: String) {
        binding.nameTopicItem.text = desVn.toString().trim()
        val cdesEng = desEng.trim()
        if (img != null){
            ImageDAO().getImage(img.toString(), binding.imageView4, "images")
        }
        binding.btnSubmit.setOnClickListener {
            val enterKey = binding.editResult.text.toString().trim()

            if (enterKey.equals(cdesEng, ignoreCase = true)) {
                // Bước 1: Kiểm tra enterKey có trùng với cdesEng không bất kể chữ in hoa.
                binding.resultname.text = "Exactly: $desEng"
                scorePlus += 30
                amountCorrect++
                binding.score.text = scorePlus.toString()
                binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.strongGreen))
                binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                // Bước 2: Nếu enterKey trùng với cdesEng, thì btnSubmit có text là "Next".

                binding.btnSubmit.setOnClickListener {
                    if (binding.btnSubmit.text == "Next") {
                        // Bước 3: Nếu btnSubmit có text là "Next", thì thực hiện currentItemIndex++; getTopic(currentItemIndex).
                        currentItemIndex++
                        getTopic(currentItemIndex)
                        binding.btnSubmit.text = "Check"
                        binding.editResult.text = null

                        // Sau đó, btnSubmit sẽ có text là "Check".
                    }else if (binding.btnSubmit.text == "Finish") {
                        // Bước 4: Nếu btnSubmit có text là "Finish", chuyển sang activity mới.
                        performActivityTransfer()
                    }
                }
            } else {
                // Bước 1: Kiểm tra enterKey không trùng với cdesEng không bất kể chữ in hoa.
                binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                binding.resultname.text = "Correct answer: $desEng"
                scorePlus += 0
                binding.score.text = scorePlus.toString()
                binding.editResult.isEnabled = false

                binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.warningRed))


                // Bước 2: Nếu enterKey không trùng với cdesEng, thì btnSubmit có text là "Next".

                binding.btnSubmit.setOnClickListener {
                    if (binding.btnSubmit.text == "Next") {
                        binding.editResult.isEnabled = true
                        binding.editResult.text = null
                        // Bước 3: Nếu btnSubmit có text là "Next", thì thực hiện currentItemIndex++; getTopic(currentItemIndex).
                        currentItemIndex++
                        getTopic(currentItemIndex)
                        binding.btnSubmit.text = "Check"
                        // Sau đó, btnSubmit sẽ có text là "Check".
                    }else if (binding.btnSubmit.text == "Finish") {
                        // Bước 4: Nếu btnSubmit có text là "Finish", chuyển sang activity mới.
                        performActivityTransfer()
                    }
                }
            }
        }
    }

    private fun performActivityTransfer() {
        // Your code to transfer to the new activity goes here
        val intent = Intent(this, ResultPointsActivity::class.java)

        val time = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val formattedTime = time.format(formatter)
        val type = "textword"


        // Use a proper DateTime object
        // Call resultSave with individual parameters
        val resultTestDAO = ResultTestDAO()
        val success = resultTestDAO.resultSave(id!!, amountCorrect, duration, formattedTime , type)

        if (success) {
            intent.putExtra("textEndQues", textEndQues)
            intent.putExtra("amountCorrect", amountCorrect)
            intent.putExtra("duration", duration) // corrected duplicate key
            intent.putExtra("scorePlus", scorePlus)
            finish()
            startActivity(intent)
        }
    }
}
