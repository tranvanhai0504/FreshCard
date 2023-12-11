package com.example.freshcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.adapters.CardStackAdapter
import com.example.freshcard.databinding.ActivityFlashCardLearnBinding
import com.example.freshcard.utils.ItemDiffCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.RewindAnimationSetting
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting
import com.yuyakaido.android.cardstackview.SwipeableMethod
import kotlinx.coroutines.runBlocking
import java.util.Stack


class FlashCardLearnActivity : AppCompatActivity(), CardStackListener {
    private lateinit var binding: ActivityFlashCardLearnBinding
    var topic : Topic = Topic()
    private var index = 1
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.cardStackView1) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(this, tts = getTTS()) }
    private var directionSwiped : Stack<Direction> = Stack<Direction>()
    private var isAutoPlay : Boolean = false


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

        setupButton()
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
        updateProcessBar(index)


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
            index = 1
        }else{
            index++
        }
        binding.txtIndex.text = index.toString() + "/" +topic.items?.size
        updateProcessBar(index)

        if (direction != null) {
            directionSwiped.push(direction)
        }
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter.itemCount - 3) {
            paginate()
        }
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {

    }

    override fun onCardDisappeared(view: View?, position: Int) {

    }

    private fun updateProcessBar(index : Int){
        binding.progressBar.progress = ((index.toFloat() / topic.items?.size!!.toFloat()) * 100).toInt()
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

    private fun setupButton() {
        val auto = findViewById<FloatingActionButton>(R.id.btnAuto)
        auto.setOnClickListener{
            if(!isAutoPlay){
                startAutoPlay()
            }else{
                isAutoPlay = false
            }
        }


        val back = findViewById<FloatingActionButton>(R.id.btnRollback)
        back.setOnClickListener {
            if(!directionSwiped.empty()){
                val setting = RewindAnimationSetting.Builder()
                    .setDirection(directionSwiped.pop())
                    .setDuration(Duration.Normal.duration)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
                manager.setRewindAnimationSetting(setting)
                cardStackView.rewind()
                if(index == 1){
                    index = topic.items?.size!!
                }else{
                    index--
                }
                binding.txtIndex.text = index.toString() + "/" +topic.items?.size
                updateProcessBar(index)
            }
        }


        val shuffle = findViewById<FloatingActionButton>(R.id.btnShuffle)
//        shuffle .setOnClickListener {
//            val setting = SwipeAnimationSetting.Builder()
//                .setDirection(Direction.Right)
//                .setDuration(Duration.Normal.duration)
//                .setInterpolator(AccelerateInterpolator())
//                .build()
//            manager.setSwipeAnimationSetting(setting)
//            cardStackView.swipe()
//        }
    }

    fun startAutoPlay(){
        isAutoPlay = false
        val backgroundThread = Thread {
            isAutoPlay = true

            while(isAutoPlay){
                runBlocking {
                    Thread.sleep(1000)
                    runOnUiThread {
                        Log.i("check", manager.topView.isDirty.toString())
                        manager.topView.performClick()
                    }

                    Thread.sleep(2000)

                    runOnUiThread{
                        val setting = SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                        manager.setSwipeAnimationSetting(setting)
                        cardStackView.swipe()
                        adapter.currentIndex++
                    }
                }

            }
        }
        backgroundThread.start()
    }
}