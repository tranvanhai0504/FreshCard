package com.example.freshcard

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.example.freshcard.adapters.TopicAdapter
import androidx.appcompat.widget.PopupMenu
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import android.widget.TextView
import androidx.core.view.isVisible
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
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
    private var tts: TextToSpeech? = null
    private var userId: String = ""
    private var directionSwiped : Stack<Direction> = Stack<Direction>()
    private var isAutoPlay : Boolean = false
    private var currentList : ArrayList<TopicItem> = ArrayList<TopicItem>()
    private var mode = "all"
    private lateinit var dialog: Dialog
    private lateinit var btnFillTest:Button
    private lateinit var btnPickerTest:Button
    private lateinit var btnCloseDialog:ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.learning_type_dialog)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog.window!!.setDimAmount(0.2F)
        dialog.window!!.setGravity(Gravity.CENTER)
        btnFillTest = dialog.findViewById(R.id.btnTextTest)
        btnPickerTest = dialog.findViewById(R.id.btnPickerTest)
        btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog)

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
            if(topic.items?.size!! < 4 ){
                btnPickerTest.alpha = 0.5f
                btnPickerTest.isEnabled = false
            }
            dialog.show()
        }

        setupButton()

        btnFillTest.setOnClickListener{
            var intent = Intent(this, ChooseTypeActivity::class.java)
            intent.putExtra("idTopicTest", id)
            intent.putExtra("testType", "text")
            startActivityForResult(intent, 100)
            dialog.dismiss()
        }

        btnPickerTest.setOnClickListener{
            var intent = Intent(this, ChooseTypeActivity::class.java)
            intent.putExtra("topic", topic)
            intent.putExtra("testType", "multipleChoices")
            startActivityForResult(intent, 100)
            dialog.dismiss()

        }

        btnCloseDialog.setOnClickListener{
            dialog.dismiss()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 100 && data!!.getBooleanExtra("isFinish", false)) {
//            setResult(RESULT_OK)
//            finish()
//        }
    }

    private fun handleOptionButton() {
        binding.btnOption.isVisible = topic.owner == userId
    }

    private fun getAllItem() {
        currentList = if(topic.items !== null){
            topic.items!!
        }else{
            ArrayList<TopicItem>()
        }
    }

    private fun getStarItems(){
        var idStarList = MainActivity.user.learningTopics?.find {
            it.idTopic == topic.id
        }?.idChecked

        var starList = topic.items?.filter {
            idStarList?.contains(it.id) == true
        }

        currentList = starList as ArrayList<TopicItem>
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

                R.id.learningPopupmenuRank-> {
                    var intent = Intent(this, RankingDetailActivity::class.java)
                    intent.putExtra("topicId", topic.id)
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

    private fun getTopic(id : String){
        TopicDAO().getTopicById(id){
            topic = it
            adapter.setTopicId(it.id)
            updateUi(topic)
            getAllItem()
            if(topic.learnedPeople?.contains(userId) !== true){
                topic.learnedPeople?.let { it1 -> TopicDAO().addLearner(userId, topic.id, it1) }
            }
        }
    }

    private fun updateUi(topicData : Topic){
        binding.txtTopicName.text = topicData.title
        currentList = topic.items!!
        binding.txtIndex.text = index.toString() + "/" + currentList?.size
        updateProcessBar(index)

        var allCardBtn = binding.btnAllCard
        allCardBtn.setTextColor(Color.parseColor("#ffffff"))
        allCardBtn.setBackgroundResource(R.drawable.switch_button_active)

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
        currentList?.let { adapter.setList(it) }
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
        if(index + 1 > currentList.size!!){
            index = 1
        }else{
            index++
        }
        binding.txtIndex.text = index.toString() + "/" + currentList.size
        updateProcessBar(index)

        if (direction != null) {
            directionSwiped.push(direction)
        }

        if (manager.topPosition == adapter.itemCount - 3) {
            if(mode != "star"){
                paginate()
            }
        }
        updateProcessBar(index)
    }

    override fun onCardRewound() {

    }

    override fun onCardCanceled() {

    }

    override fun onCardAppeared(view: View?, position: Int) {
        view?.findViewById<FrameLayout>(R.id.card_container)?.alpha = 1F
        view?.findViewById<FrameLayout>(R.id.card_container)?.rotationX = 0F
        view?.findViewById<FrameLayout>(R.id.card_container2)?.alpha = 0F
        view?.findViewById<FrameLayout>(R.id.card_container2)?.rotationX = 180F
    }

    override fun onCardDisappeared(view: View?, position: Int) {

        view?.findViewById<FrameLayout>(R.id.card_container)?.alpha = 1F
        view?.findViewById<FrameLayout>(R.id.card_container)?.rotationX = 0F
        view?.findViewById<FrameLayout>(R.id.card_container2)?.alpha = 0F
        view?.findViewById<FrameLayout>(R.id.card_container2)?.rotationX = 180F

    }

    private fun updateProcessBar(index : Int){
        binding.progressBar.progress = ((index.toFloat() / currentList?.size!!.toFloat()) * 100).toInt()
    }

    private fun paginate() {
        val old = adapter.getList()
        val new = ArrayList<TopicItem>()
        new.addAll(old)
        currentList?.let { new.addAll(it) }
        val callback = ItemDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setList(new)
        result.dispatchUpdatesTo(adapter)
    }

    private fun setupButton() {
        val auto = findViewById<FloatingActionButton>(R.id.btnAuto)
        auto.setOnClickListener{
            if(adapter.itemCount < 4){
                Toast.makeText(this, "This topic is not enough item to run this feature", Toast.LENGTH_SHORT).show()

            }else {
                if (!isAutoPlay) {
                    startAutoPlay()
                    auto.setImageResource(R.drawable.stop_fill)
                    binding.txtPlayShow.text = "Stop"
                } else {
                    isAutoPlay = false
                    auto.setImageResource(R.drawable.arrow_drop_down_big)
                    binding.txtPlayShow.text = "Auto play"
                }
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
                    index = currentList?.size!!
                }else{
                    index--
                }
                binding.txtIndex.text = index.toString() + "/" + currentList.size
                updateProcessBar(index)
            }
        }


        val shuffle = findViewById<FloatingActionButton>(R.id.btnShuffle)
        shuffle.setOnClickListener {
            adapter.shuffle()
        }

        var starCardBtn = binding.btnStarCard
        var allCardBtn = binding.btnAllCard
        starCardBtn.setOnClickListener {
            starCardBtn.setTextColor(Color.parseColor("#ffffff"))
            starCardBtn.setBackgroundResource(R.drawable.switch_button_active)
            allCardBtn.setTextColor(Color.parseColor("#B0B0B0"))
            allCardBtn.setBackgroundResource(R.drawable.switch_button)

            mode = "star"
            getStarItems()
            adapter.setList(currentList)
            adapter.notifyDataSetChanged()

            directionSwiped.clear()

            index = 1
            binding.txtIndex.text = index.toString() + "/" + currentList.size
            updateProcessBar(index)
        }

        allCardBtn.setOnClickListener {
            allCardBtn.setTextColor(Color.parseColor("#ffffff"))
            allCardBtn.setBackgroundResource(R.drawable.switch_button_active)
            starCardBtn.setTextColor(Color.parseColor("#B0B0B0"))
            starCardBtn.setBackgroundResource(R.drawable.switch_button)

            mode = "all"
            getAllItem()
            adapter.setList(currentList)
            adapter.notifyDataSetChanged()

            directionSwiped.clear()

            index = 1
            binding.txtIndex.text = index.toString() + "/" +currentList.size
            updateProcessBar(index)
        }
    }

    private fun startAutoPlay(){
        isAutoPlay = false
        val backgroundThread = Thread {
            isAutoPlay = true
            runBlocking {
                while(isAutoPlay){
                    if(manager.topView.findViewById<FrameLayout>(R.id.card_container).alpha == 1F){
                        Thread.sleep(2000)
                        if(!isAutoPlay){
                            break
                        }
                        runOnUiThread {
                            manager.topView.performClick()
                        }
                    }

                    Thread.sleep(2000)
                    if(!isAutoPlay){
                        break
                    }
                    runOnUiThread {
                        manager.topView.performClick()
                    }

                    Thread.sleep(400)
                    if(!isAutoPlay){
                        break
                    }
                    runOnUiThread{
                        val setting = SwipeAnimationSetting.Builder()
                            .setDirection(Direction.Left)
                            .setDuration(Duration.Normal.duration)
                            .setInterpolator(AccelerateInterpolator())
                            .build()
                        manager.setSwipeAnimationSetting(setting)
                        cardStackView.swipe()
                    }
                }
            }
        }
        backgroundThread.start()
    }
}