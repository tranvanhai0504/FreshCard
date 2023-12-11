package com.example.freshcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
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

class FlashCardLearnActivity : AppCompatActivity(), CardStackListener {
    private lateinit var binding: ActivityFlashCardLearnBinding
    var topic : Topic = Topic()
    private var index = 1
    private val cardStackView by lazy { findViewById<CardStackView>(R.id.cardStackView1) }
    private val manager by lazy { CardStackLayoutManager(this, this) }
    private val adapter by lazy { CardStackAdapter(this) }

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
}