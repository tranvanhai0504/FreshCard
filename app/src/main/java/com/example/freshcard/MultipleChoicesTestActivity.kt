package com.example.freshcard

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.freshcard.DAO.HistoryDAO
import com.example.freshcard.DAO.TestResultDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.History
import com.example.freshcard.Structure.ResultTest
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.databinding.ActivityMultipleChoicesTestBinding
import com.example.freshcard.fragments.TopicsFragment
import com.google.type.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class MultipleChoicesTestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMultipleChoicesTestBinding
    private lateinit var timer: Job
    private lateinit var topic: Topic
    private var totalCorrect: Int = 0
    private var totalWrong: Int = 0
    private var currentIndex: Int = 0
    private var currDuration: String = ""
    private lateinit var listItems: ArrayList<TopicItem>
    private var listWords: ArrayList<String> = ArrayList(emptyList<String>())
    private var currDurationInt: Int = 0
    private var currAnswer: String = ""
    private var isEntoVie = false
    private var listTest = ArrayList<ArrayList<String>>()
    private var idLearned = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipleChoicesTestBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        topic = (intent.getSerializableExtra("topic") as? Topic)!!
        var type = intent.getStringExtra("selectedButton")
        if(type == "btnEngtovn") {
            isEntoVie = true
        }
        listItems = topic.items!!
        listItems.forEach{
            if(isEntoVie) {
                listWords.add(it.vie)
            }else {
                listWords.add(it.en)
            }
        }
        if(listWords.size<4) {
            listWords.add("word")
            listWords.add("key")
            listWords.add("how")
        }
        setCurrentView(listItems[0])

        startTimer()
        settingCheckButton(false)
        val normalButtonColor = ContextCompat.getColor(this, R.color.white)
        val normalTextColor = ContextCompat.getColor(this, R.color.grayDefault)
        val selectedButtonColor = ContextCompat.getColor(this, R.color.mediumLightGreen)
        binding.btnCheck.setOnClickListener{
            checkResult(currAnswer)
            resetButton(normalButtonColor,normalTextColor)
            nextItem()
        }

        binding.btnback.setOnClickListener {

        }

        binding.btnOption1.setOnClickListener{
            currAnswer = binding.btnOption1.text.toString()
            binding.btnOption1.backgroundTintList =  ColorStateList.valueOf(selectedButtonColor)
            binding.btnOption1.setTextColor(normalButtonColor)
            binding.btnOption2.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption3.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption4.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption2.setTextColor(normalTextColor)
            binding.btnOption3.setTextColor(normalTextColor)
            binding.btnOption4.setTextColor(normalTextColor)
            settingCheckButton(true)
        }
        binding.btnOption2.setOnClickListener{
            currAnswer = binding.btnOption2.text.toString()
            binding.btnOption1.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption2.backgroundTintList =  ColorStateList.valueOf(selectedButtonColor)
            binding.btnOption2.setTextColor(normalButtonColor)
            binding.btnOption3.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption4.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption1.setTextColor(normalTextColor)
            binding.btnOption3.setTextColor(normalTextColor)
            binding.btnOption4.setTextColor(normalTextColor)
            settingCheckButton(true)
        }
        binding.btnOption3.setOnClickListener{
            currAnswer = binding.btnOption3.text.toString()
            binding.btnOption1.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption2.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption3.backgroundTintList =  ColorStateList.valueOf(selectedButtonColor)
            binding.btnOption4.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption1.setTextColor(normalTextColor)
            binding.btnOption2.setTextColor(normalTextColor)
            binding.btnOption3.setTextColor(normalButtonColor)
            binding.btnOption4.setTextColor(normalTextColor)
            settingCheckButton(true)
        }
        binding.btnOption4.setOnClickListener{
            currAnswer = binding.btnOption4.text.toString()
            settingCheckButton(true)
            binding.btnOption1.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption2.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption3.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
            binding.btnOption4.backgroundTintList =  ColorStateList.valueOf(selectedButtonColor)
            binding.btnOption1.setTextColor(normalTextColor)
            binding.btnOption2.setTextColor(normalTextColor)
            binding.btnOption3.setTextColor(normalTextColor)
            binding.btnOption4.setTextColor(normalButtonColor)
        }
        binding.btnSubmit.setOnClickListener{
            timer.cancel()
            var userId = UserDAO().getUserIdShareRef(this)

            var result: ResultTest = ResultTest(userId,topic.id,totalCorrect,currDurationInt, (DateTime.getDefaultInstance()).toString(), "multiple choice")
            var intent = Intent(this, ShowResultActivity::class.java)
            intent.putExtra("totalItems", listItems.size)
            intent.putExtra("testResult",  ArrayList(listTest.map { it.toList() }))
            intent.putExtra("result", result)
            HistoryDAO().pushHistory(userId, topic.id, Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
            TestResultDAO().pushTestResult(ResultTest(userId,topic.id,totalCorrect,currDurationInt,(DateTime.getDefaultInstance()).toString(), "Multiple Choices"))
            UserDAO().pushLearnedTopic(idLearned, userId, topic.id)

            startActivityForResult(intent, 100)
        }

        binding.btnback.setOnClickListener{
            confirmExit()
        }

        binding.btnHome.setOnClickListener{
            confirmExit()
        }

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
                    timer.cancel()
                    finish()

            }
            .setNegativeButton("NO") {
                    dialog, which ->
                        timer.cancel()
                        dialog.dismiss()


            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

    private fun settingCheckButton(bool: Boolean) {
        if(bool) {
            binding.btnCheck.alpha = 1f
        }else {
            binding.btnCheck.alpha = 0.6f
        }
        binding.btnCheck.isEnabled = bool
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            var isAgain = data!!.getBooleanExtra("isAgain", false)
            if(isAgain) {
                resetTest()
            }else {
                var intent = Intent()
                intent.putExtra("isFinish", true)
                setResult(Activity.RESULT_OK)
                setResult(100, intent)
                timer.cancel()
                finish()
            }
        }
    }

    private fun resetButton(normalButtonColor: Int,normalTextColor: Int) {
        binding.btnOption1.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
        binding.btnOption2.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
        binding.btnOption3.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
        binding.btnOption4.backgroundTintList =  ColorStateList.valueOf(normalButtonColor)
        binding.btnOption1.setTextColor(normalTextColor)
        binding.btnOption2.setTextColor(normalTextColor)
        binding.btnOption3.setTextColor(normalTextColor)
        binding.btnOption4.setTextColor(normalTextColor)
    }
    private fun checkResult(value: String) {
        var item = listItems[currentIndex]

        var word = item.en
        var learingWord = item.vie
        if(isEntoVie) {
            word = item.vie
            learingWord = item.en
        }
        if(value == word) {
            totalCorrect+=1
            binding.txtTotalCorrect.text = "${totalCorrect}"
            idLearned.add(item.id)


        }else {
            totalWrong +=1
            binding.txtTotalWrong.text = "${totalWrong}"
        }
        var ls = ArrayList<String>()
        ls.add(learingWord)
        ls.add(value)
        ls.add(word)
        listTest.add(ls)

    }

    private fun setCurrentView(item: TopicItem) {
        if(isEntoVie) {
            listWords.remove(item.vie)
        }else {
            listWords.remove(item.en)
        }
        listWords.shuffle()
        var randomWords:ArrayList<String> = ArrayList(listWords.take(3))
        if(isEntoVie) {
            randomWords.add(item.vie)
        }else {
            randomWords.add(item.en)
        }
        randomWords.shuffle()
        binding.btnOption1.text = randomWords.get(0)
        binding.btnOption2.text = randomWords.get(1)
        binding.btnOption3.text = randomWords.get(2)
        binding.btnOption4.text = randomWords.get(3)
        listWords = ArrayList(emptyList<String>())
        if(isEntoVie) {
            binding.txtCurrWord.text = item.en
            listItems.forEach{
                listWords.add(it.vie)
            }
        }else {
            binding.txtCurrWord.text = item.vie
            listItems.forEach{
                listWords.add(it.en)
            }
        }
    }

    private fun nextItem() {
        currAnswer = ""
        settingCheckButton(false)
        currentIndex+=1
        if(currentIndex>=listItems.size) {
            binding.btnSubmit.isVisible = true
            binding.btnCheck.isVisible = false
            binding.btnOption1.isVisible = false
            binding.btnOption2.isVisible = false
            binding.btnOption3.isVisible = false
            binding.btnOption4.isVisible = false
            binding.contentTitle.isVisible = false
            binding.txtCurrWord.text = "Done!"
            binding.progress.progress = 100
            binding.progressState.text = "100"

        }else {
            setCurrentView(listItems[currentIndex])
            binding.progress.progress = (currentIndex*100/listItems.size)
            binding.progressState.text = "${currentIndex*100/listItems.size}"
        }
    }

    private fun startTimer() {
        timer = GlobalScope.launch(Dispatchers.Main) {
            var i = 0
            while (i <= 6000) {
                val time = when {
                    i < 10 -> "00:0$i"
                    i < 60 -> "00:$i"
                    i < 600 -> if (i % 60 < 10) "0${i / 60}:0${i % 60}" else "0${i / 60}:${i % 60}"
                    else -> "${i / 600}:${i % 600}"
                }
                binding.txtTimer.text = time
                currDuration = time
                delay(1000)
                i += 1
                currDurationInt = i
            }
        }

    }

    fun resetTest() {
        val normalButtonColor = Color.parseColor("#FFFFFF")
        val normalTextColor = Color.parseColor("#B0B0B0")
        totalCorrect = 0
        totalWrong = 0
        currentIndex = -1
        nextItem()
        resetButton(normalButtonColor,normalTextColor)
        settingCheckButton(false)
        binding.txtTotalCorrect.text = "0"
        binding.txtTotalWrong.text = "0"
        binding.progressState.text = "0"
        binding.progress.progress = 0

        binding.btnSubmit.isVisible = false
        binding.btnCheck.isVisible = true
        binding.btnOption1.isVisible = true
        binding.btnOption2.isVisible = true
        binding.btnOption3.isVisible = true
        binding.btnOption4.isVisible = true
        binding.contentTitle.isVisible = true
        startTimer()
    }

}