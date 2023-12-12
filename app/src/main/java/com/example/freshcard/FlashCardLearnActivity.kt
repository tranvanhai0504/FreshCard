package com.example.freshcard

import android.app.Dialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.freshcard.adapters.TopicAdapter
import androidx.appcompat.widget.PopupMenu
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.adapters.CardStackAdapter
import com.example.freshcard.databinding.ActivityFlashCardLearnBinding
import com.example.freshcard.utils.ItemDiffCallback
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import java.util.Locale

class FlashCardLearnActivity : AppCompatActivity(), CardStackListener {
    private lateinit var binding: ActivityFlashCardLearnBinding
    var topic : Topic = Topic()
    private var index = 1
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.cardStackView1) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(this, tts = getTTS()) }
    private var tts: TextToSpeech? = null
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var dialog: Dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.learning_type_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setDimAmount(0.2F)
        dialog.window!!.setGravity(Gravity.CENTER)
        var btnFillTest:Button = dialog.findViewById(R.id.btnTextTest)
        var btnPickerTest:Button = dialog.findViewById(R.id.btnPickerTest)
        var btnCloseDialog:ImageButton = dialog.findViewById(R.id.btnCloseDialog)

        binding = ActivityFlashCardLearnBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //get id topic
        var id = intent.getStringExtra("idTopic")

        //get id user
        val sharedPreferences = this.applicationContext?.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        userId = sharedPreferences?.getString("idUser", "undefined")!!

        //create learning topic
        if (id != null) {
            UserDAO().addLearningTopic(userId, id)
        }

        //get topic
        if (id != null) {
            getTopic(id)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnOption.setOnClickListener{
            showPopupMenu(id!!)

        }

        binding.btnTest.setOnClickListener {
            dialog.show()
        }

        btnFillTest.setOnClickListener{
            Toast.makeText(this, "test with fill in blank", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        btnPickerTest.setOnClickListener{
            Toast.makeText(this, "test with multiple choices", Toast.LENGTH_SHORT).show()
            dialog.dismiss()

        }

        btnCloseDialog.setOnClickListener{

            dialog.dismiss()
        }


        binding.btnTest.setOnClickListener {
            var intent = Intent(this, ChooseTypeActivity::class.java)
            intent.putExtra("idTopicTest", id)
            startActivity(intent)
        }
    }

    private fun handleOptionButton() {
        binding.btnOption.isVisible = topic.owner == userId
        Log.e("option", "${topic.owner} -- ${userId}")

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

    fun getTopic(id : String){
        TopicDAO().getTopicById(id){
            topic = it
            updateUi(topic)
        }
    }

    fun updateUi(topicData : Topic){
        binding.txtTopicName.text = topicData.title
        binding.txtIndex.text = index.toString() + "/" + topicData.items?.size
        updateProcessBar()


        manager.setStackFrom(StackFrom.Bottom)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        topicData.items?.let { adapter.setList(it) }
        cardStackView.adapter = adapter
        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        handleOptionButton()
    }

    private fun getTTS(): TextToSpeech {
        val textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // TTS initialized successfully
            }
        }
        return textToSpeech
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        if(index + 1 > topic.items?.size!!){
            binding.txtIndex.text = "1" + "/" +topic.items?.size
            index = 1
        }else{
            index++
            binding.txtIndex.text = index.toString() + "/" +topic.items?.size
        }

        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 3) {
            paginate()
        }
        updateProcessBar()
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    private fun updateProcessBar(){
        binding.progressBar.progress = index / topic.items?.size!! * 100
    }

    private fun paginate() {
        val old = adapter.getList()
        val new = ArrayList<TopicItem>()
        new.addAll(old)
        topic.items?.let { new.addAll(it) }
        val callback = ItemDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setList(new)
        result.dispatchUpdatesTo(adapter)
    }

    //text to speech
//    override fun onInit(status: Int) {
//        if (status == TextToSpeech.SUCCESS) {
//            val result = tts!!.setLanguage(Locale.US)
//
//            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS","The Language not supported!")
//            } else {
////                btnSpeak!!.isEnabled = true
//            }
//        }
//    }
//    public override fun onDestroy() {
//        // Shutdown TTS when
//        // activity is destroyed
//        if (tts != null) {
//            tts!!.stop()
//            tts!!.shutdown()
//        }
//        super.onDestroy()
//    }

}