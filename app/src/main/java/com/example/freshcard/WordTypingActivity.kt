package com.example.freshcard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.freshcard.DAO.HistoryDAO
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.TestResultDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.ResultTest
import com.example.freshcard.Structure.Topic
import com.example.freshcard.databinding.ActivityWordTypingBinding
import com.google.type.DateTime
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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
    var amountIncorrect = 0
    private val correctAnswers = mutableListOf<String>()
    private val incorrectAnswers = mutableListOf<String>()
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
            confirmExit()
        }

        binding.btnBackHome.setOnClickListener {
            val idTopic = intent.getStringExtra("idTopicTest")
            val intent = Intent(this, FlashCardLearnActivity::class.java)
                intent.putExtra("idTopic", idTopic)
            finish()
            startActivity(intent)
        }

        binding.score.text = "0"

    }

    private fun confirmExit() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Stop testing?")
            .setTitle("Exit")
            .setPositiveButton("YES") {
                    dialog, which ->
                setResult(Activity.RESULT_OK)
                var intent = Intent()
                intent.putExtra("isFinish", true)
                setResult(100, intent)
                finish()

            }
            .setNegativeButton("NO") {
                    dialog, which ->
                dialog.dismiss()


            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

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

        // Ban đầu, set màu sắc của btnSubmit dựa trên trạng thái của editResult.
        updateButtonColor(binding.editResult.text.toString().trim())

        // Thêm TextWatcher để theo dõi sự thay đổi trong editResult.
        binding.editResult.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // Sau khi người dùng nhập liệu, cập nhật màu sắc của btnSubmit.
                updateButtonColor(editable.toString().trim())
            }
        })

        binding.btnSubmit.setOnClickListener {
            val enterKey = binding.editResult.text.toString().trim()
            if (enterKey.isNotEmpty()){
                if (enterKey.equals(cdesVn, ignoreCase = true)) {
                    // Bước 1: Kiểm tra enterKey có trùng với cdesEng không bất kể chữ in hoa.
                    binding.resultname.text = "Exactly: $desVn"
                    scorePlus += 30
                    amountCorrect++
                    binding.score.text = scorePlus.toString()
                    binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.strongGreen))
                    correctAnswers.add("$desEng - $desVn")
                    Log.e("correctAnswers", "correctAnswers: $correctAnswers", )
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
                }
                else {
                    // Bước 1: Kiểm tra enterKey không trùng với cdesEng không bất kể chữ in hoa.
                    binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                    binding.resultname.text = "Correct answer: $desVn"
                    scorePlus += 0
                    amountIncorrect++
                    binding.score.text = scorePlus.toString()
                    binding.editResult.isEnabled = false
                    binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.warningRed))
                    incorrectAnswers.add("$desEng - $desVn")

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
    }

    private fun vnToEng(desVn: String, desEng: String, img: String) {
        binding.nameTopicItem.text = desVn.toString().trim()

        val cdesEng = desEng.trim()

        // Ban đầu, set màu sắc của btnSubmit dựa trên trạng thái của editResult.
        updateButtonColor(binding.editResult.text.toString().trim())

        // Thêm TextWatcher để theo dõi sự thay đổi trong editResult.
        binding.editResult.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                // Sau khi người dùng nhập liệu, cập nhật màu sắc của btnSubmit.
                updateButtonColor(editable.toString().trim())
            }
        })

        binding.btnSubmit.setOnClickListener {
            val enterKey = binding.editResult.text.toString().trim()

            if (enterKey.isNotEmpty()) {
                if (enterKey.equals(cdesEng, ignoreCase = true)) {
                    binding.resultname.text = "Exactly: $desEng"
                    scorePlus += 30
                    amountCorrect++
                    binding.score.text = scorePlus.toString()
                    binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.strongGreen))
                    correctAnswers.add("$desEng - $desVn")
                    Log.e("correctAnswers", "correctAnswers: $correctAnswers", )
                    binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"

                    binding.btnSubmit.setOnClickListener {
                        if (binding.btnSubmit.text == "Next") {
                            currentItemIndex++
                            getTopic(currentItemIndex)
                            binding.btnSubmit.text = "Check"
                            binding.editResult.text = null
                            updateButtonColor("") // Cập nhật màu sắc của btnSubmit khi bắt đầu một câu mới.
                        } else if (binding.btnSubmit.text == "Finish") {
                            performActivityTransfer()
                        }
                    }
                }else {
                    binding.btnSubmit.text = if (currentItemIndex == checkcurrentItemIndex) "Finish" else "Next"
                    binding.resultname.text = "Correct answer: $desEng"
                    scorePlus += 0
                    binding.score.text = scorePlus.toString()
                    binding.editResult.isEnabled = false
                    amountIncorrect++
                    binding.resultname.setTextColor(ContextCompat.getColor(this, R.color.warningRed))
                    incorrectAnswers.add("$desEng - $desVn")
                    binding.btnSubmit.setOnClickListener {
                        if (binding.btnSubmit.text == "Next") {
                            binding.editResult.isEnabled = true
                            binding.editResult.text = null
                            currentItemIndex++
                            getTopic(currentItemIndex)
                            binding.btnSubmit.text = "Check"
                            updateButtonColor("") // Cập nhật màu sắc của btnSubmit khi bắt đầu một câu mới.
                        } else if (binding.btnSubmit.text == "Finish") {
                            performActivityTransfer()
                        }
                    }
                }
            }
        }
    }
    private fun updateButtonColor(enteredText: String) {
        if (enteredText.isEmpty()) {
            binding.btnSubmit.backgroundTintList = resources.getColorStateList(R.color.grayDefault)
        } else {
            binding.btnSubmit.backgroundTintList = resources.getColorStateList(R.color.mediumGreen)
        }
    }

    private fun performActivityTransfer() {

        var userId = UserDAO().getUserIdShareRef(this)
        val idTopic = intent.getStringExtra("idTopicTest")

        if (idTopic != null) {
            HistoryDAO().pushHistory(userId, idTopic, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
        }

        TestResultDAO().pushTestResult(ResultTest(userId,amountCorrect,duration,(DateTime.getDefaultInstance()).toString(), "Enter word"))


        val idTopics = intent.getStringExtra("idTopicTest")
        val intent = Intent(this, ResultPointsActivity::class.java)
            intent.putExtra("idTopic", idTopics)
            intent.putExtra("textEndQues", textEndQues)
            intent.putExtra("amountCorrect", amountCorrect)
            intent.putExtra("duration", duration) // corrected duplicate key
            intent.putExtra("scorePlus", scorePlus)
            finish()
            startActivity(intent)

        // Truyền mảng incorrectAnswers đến hàm lưu trạng thái
        saveIncorrectAnswers(incorrectAnswers, correctAnswers)
    }

    private fun saveIncorrectAnswers(incorrectAnswers: List<String>, correctAnswers: List<String>) {
        // Lưu mảng incorrectAnswers vào SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("your_prefs_name", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("incorrectAnswers", HashSet(incorrectAnswers))
        editor.putStringSet("correctAnswers", HashSet(correctAnswers))
        editor.apply()
    }


}
