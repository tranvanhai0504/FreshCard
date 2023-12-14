package com.example.freshcard.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.FlashCardLearnActivity
import com.example.freshcard.MainActivity
import com.example.freshcard.R
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.TopicItem
import java.util.Locale

class CardStackAdapter(
    var context : Context,
    private var items: ArrayList<TopicItem> = ArrayList<TopicItem>(),
    private val tts: TextToSpeech,
    private var topicId : String = ""
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {
    lateinit var flip_animation : AnimatorSet
    lateinit var flip_animation_back : AnimatorSet

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.learning_card, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var isFront = true

        holder.textCard.text = item.en
        holder.textCardBack.text = item.vie

        val scale = context.applicationContext.resources.displayMetrics.density

        holder.cardFrontContainer.cameraDistance = 8000 * scale
        holder.cardBackContainer.cameraDistance = 8000 * scale

        flip_animation = AnimatorInflater.loadAnimator(context.applicationContext, R.animator.flip_animation) as AnimatorSet
        flip_animation_back = AnimatorInflater.loadAnimator(context.applicationContext, R.animator.flip_animation_back) as AnimatorSet

        holder.itemView.setOnClickListener {
            if(!flip_animation.isRunning || !flip_animation_back.isRunning){
                if(isFront){
                    flip_animation.setTarget(holder.cardFrontContainer)
                    flip_animation_back.setTarget(holder.cardBackContainer)
                    flip_animation.start()
                    flip_animation_back.start()
                    isFront = false
                }else{
                    flip_animation.setTarget(holder.cardBackContainer)
                    flip_animation_back.setTarget(holder.cardFrontContainer)
                    flip_animation.start()
                    flip_animation_back.start()
                    isFront = true
                }
            }
        }

        holder.voiceBtn.setOnClickListener{
            speakOut(item.en)
        }

        var topicChecked = MainActivity.Companion.user.learningTopics?.find {
            it.idTopic == topicId
        }?.idChecked

        if (topicChecked != null) {
            if(topicChecked.contains(item.id)){
                holder.starBtn.setBackgroundResource(R.drawable.star_fill)
            }else{
                holder.starBtn.setBackgroundResource(R.drawable.star)
            }
        }

        holder.starBtn.setOnClickListener {
            var learning = MainActivity.Companion.user.learningTopics

            learning?.forEach { it ->
                if(it.idTopic == topicId){
                    if(!it.idChecked.contains(item.id)){
                        it.idChecked.add(item.id)
                        holder.starBtn.setBackgroundResource(R.drawable.star_fill)
                    }else{
                        it.idChecked.remove(item.id)
                        holder.starBtn.setBackgroundResource(R.drawable.star)
                    }
                }
            }

            if (learning != null) {
                UserDAO().updateStarTopicItem(learning)
            }
        }

        if(item.image != ""){
            ImageDAO().getImage(item.image, holder.image, "images")
            holder.textCard
        }
    }

    fun shuffle(){
        items.shuffle()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(itemsList: ArrayList<TopicItem>) {
        this.items = itemsList
    }

    fun getList() : ArrayList<TopicItem>{
        return items
    }

    fun setTopicId(id : String){
        topicId = id
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardFrontContainer = view.findViewById<FrameLayout>(R.id.card_container)
        var cardBackContainer = view.findViewById<FrameLayout>(R.id.card_container2)


        var textCard = view.findViewById<TextView>(R.id.textCard)
        var starBtn = view.findViewById<ImageButton>(R.id.btn_star)
        var voiceBtn = view.findViewById<ImageButton>(R.id.btn_voice)
        var image = view.findViewById<ImageView>(R.id.img)

        var textCardBack = view.findViewById<TextView>(R.id.textCard_card_back)
        var imageBack = view.findViewById<ImageView>(R.id.img_card_back)
    }





}
