package com.example.freshcard

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.freshcard.Adapter.CardAdapter
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.adapters.TopicAdapter
import com.example.freshcard.fragments.TopicsFragment
import kotlinx.coroutines.runBlocking
import org.apache.commons.csv.CSVFormat
import java.io.InputStream
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Calendar

class AddTopic : AppCompatActivity() {
    private lateinit var cardAdapter: CardAdapter
    private lateinit var cardsRectyclerView: RecyclerView
    private lateinit var btnEditName: ImageButton
    private lateinit var inputTopicName: EditText
    private lateinit var btnBackToHome: ImageButton
    private lateinit var btnSaveTopicName: ImageButton
    private lateinit var btnAddCard: Button
    private lateinit var btnSubmitTopic: Button
    private lateinit var btnImport: Button
    private lateinit var imageUri: Uri
    private lateinit var switchButton: Switch

    private var learnedPeoples = ArrayList<String>()
    private var isPublic = false
    var adapterData =  ArrayList<TopicItem>()
    var currTopicId = ""
    var userId = ""
    var context = this
    var isEditing = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_topic)
        cardsRectyclerView = findViewById(R.id.cardsReccylerView)
        btnEditName = findViewById(R.id.btnEditTopicName)
        inputTopicName = findViewById(R.id.inputTopicName)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        btnSaveTopicName = findViewById(R.id.btnSaveTopicName)
        btnAddCard = findViewById(R.id. btnAddCard)
        btnSubmitTopic = findViewById(R.id.btnSubmitTopic)
        btnImport = findViewById(R.id.btnImport)
        switchButton = findViewById(R.id.switchButton)

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                switchButton.text = "Public"
                isPublic=true
            } else {
                switchButton.text = "Private"
                isPublic=false
            }
        }
        btnSaveTopicName.isVisible = false
        cardsRectyclerView.layoutManager = LinearLayoutManager(this)


        var topicId: String = intent.getStringExtra("edit")!!
        if(topicId != "false") {
            TopicDAO().getTopicById(topicId) { topic->
                adapterData = topic.items!!
                currTopicId = topicId
                isEditing = true
                learnedPeoples = topic.learnedPeople!!
                cardAdapter =CardAdapter(adapterData, this, currTopicId){  -> selectImage() }
                cardsRectyclerView.adapter = cardAdapter
                inputTopicName.setText(topic.title)
                if(topic.isPublic) {
                    switchButton.alpha = 0.4F
                    switchButton.isEnabled = false
                    switchButton.text = "Public"
                    switchButton.isChecked = true
                }
            }
        }else {
            currTopicId = "${getCurrentTimeInDecimal()}${userId}"
            cardAdapter =CardAdapter(adapterData, this, currTopicId){  -> selectImage() }
            cardsRectyclerView.adapter = cardAdapter
        }

        val sharedPreferences = applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences.getString("idUser", "undefined")!!

        btnImport.setOnClickListener{
            v->
            pickFile.launch("*/*")
        }

        btnAddCard.setOnClickListener{
            v->
            if(cardAdapter.isEditing) {
                cardAdapter.confirmChange()
            }else {
                cardAdapter.createEmptyCard()
            }
        }
        btnSubmitTopic.setOnClickListener{
            v->
            saveTopic()
        }
        btnBackToHome.setOnClickListener{
            v->
            finish()
        }

        btnSaveTopicName.setOnClickListener{
            v->
            handleSaveName()
        }

        btnEditName.setOnClickListener {
            handleClickEditName()
        }


    }

    private fun handleClickEditName() {
        inputTopicName.isEnabled = true
        btnEditName.isVisible = false
        btnSaveTopicName.isVisible = true
        inputTopicName.requestFocus()

    }

    private fun handleSaveName() {
        inputTopicName.isEnabled = false
        btnEditName.isVisible = true
        btnSaveTopicName.isVisible = false
        inputTopicName.clearFocus()
    }

    private fun saveTopic() {
        var topic = Topic(currTopicId, userId, inputTopicName.text.toString(), adapterData, isPublic, learnedPeoples, LocalDateTime.now().toEpochSecond(
        ZoneOffset.UTC), 0)
        if(!isEditing) {
            MainActivity.Companion.user.learningTopics?.add(LearningTopic(idTopic = currTopicId, idLearning = ArrayList(emptyList<String>()), idLearned = ArrayList(emptyList<String>()), idChecked = ArrayList(emptyList<String>())))
            UserDAO().pushLearningTopic(LearningTopic(idTopic = currTopicId, idLearning = ArrayList(emptyList<String>()), idLearned = ArrayList(emptyList<String>()), idChecked = ArrayList(emptyList<String>())), userId)
        }
        UserDAO().pushTopic(topic)
        finish()
    }

    private fun getCurrentTimeInDecimal(): Int {
        val calendar = Calendar.getInstance()

        val hours = calendar.get(Calendar.HOUR_OF_DAY) * 10000 // Convert hours to seconds
        val minutes = calendar.get(Calendar.MINUTE) * 100 // Convert minutes to seconds
        val seconds = calendar.get(Calendar.SECOND)

        // Calculate the time as an integer
        val timeInteger = hours + minutes + seconds
        return timeInteger
    }


    fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    val pickFile = registerForActivityResult(ActivityResultContracts.GetContent()){
        if (it != null) {
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(it)
            var handler = TopicDAO()
            inputStream?.use { stream ->
                var lists = emptyList<TopicItem>()
                lists = readCsv(stream)
                for(item in lists) {
                    item.id = "${currTopicId}<${adapterData.size}"
                    adapterData.add(item)
                }
                cardAdapter.updateData(adapterData)
            }
            Toast.makeText(this, "Import Students successfully", Toast.LENGTH_SHORT).show()
            }
    }

    fun readCsv(inputStream: InputStream): List<TopicItem> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT).apply {
            setIgnoreSurroundingSpaces(true)
        }.build().parse(inputStream.reader()).drop(1) // Dropping the header
            .map {
                TopicItem(
                    id = "",
                    en = it[0],
                    vie = it[1],
                    description = it[2],
                    image = ""
                );
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && data != null && data.data != null) {
            imageUri = data.data!!
            var imageHandler = ImageDAO()
            var fileName = imageHandler.uploadImage(this, imageUri)
            cardAdapter.setImage(imageUri, fileName)
        }
    }

}