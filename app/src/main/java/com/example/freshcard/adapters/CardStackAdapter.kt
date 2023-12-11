package com.example.freshcard.adapters

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.freshcard.FlashCardLearnActivity
import com.example.freshcard.R
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.TopicItem
import java.util.Locale

class CardStackAdapter(
        var context : Context,
        private var items: ArrayList<TopicItem> = ArrayList<TopicItem>(),
                private val tts: TextToSpeech
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

//        var bacColors = listOf<String>("#8CDA4F","#ECE3BA", "#CCB6B6", "#B9B2CB")
//        val textColors = listOf<String>("#17594A", "#D5B04D", "#A45353", "#55498D")

//        var mainColor = bacColors[position % textColors.size]
//        var subColor = textColors[position % bacColors.size]
//
//        changeColor(holder, mainColor, subColor)

        val scale = context.applicationContext.resources.displayMetrics.density

        holder.cardFrontContainer.cameraDistance = 8000 * scale
        holder.cardBackContainer.cameraDistance = 8000 * scale

        flip_animation = AnimatorInflater.loadAnimator(context.applicationContext, R.animator.flip_animation) as AnimatorSet
        flip_animation_back = AnimatorInflater.loadAnimator(context.applicationContext, R.animator.flip_animation_back) as AnimatorSet

        holder.itemView.setOnClickListener {
            if(!flip_animation.isRunning || !flip_animation_back.isRunning){
                if(isFront){
                    flip_animation.setTarget(holder.cardBackContainer)
                    flip_animation_back.setTarget(holder.cardFrontContainer)
                    flip_animation.start()
                    flip_animation_back.start()
                    isFront = false
                }else{
                    flip_animation.setTarget(holder.cardFrontContainer)
                    flip_animation_back.setTarget(holder.cardBackContainer)
                    flip_animation.start()
                    flip_animation_back.start()
                    isFront = true
                }
            }
        }

        holder.voiceBtn.setOnClickListener{
            speakOut(item.en)
        }
    }

//    fun changeColor(holder: ViewHolder, mainColor : String, subColor : String){
//        holder.itemView.backgroundTintList =  ColorStateList.valueOf(Color.parseColor(mainColor))
//        when(mainColor){
//            "#8CDA4F" -> {
//                holder.starBtn.setBackgroundResource(R.drawable.star)
//                holder.voiceBtn.setBackgroundResource(R.drawable.sound_max)
//            }
//            "#ECE3BA" -> {
//                holder.starBtn.setBackgroundResource(R.drawable.star_1_)
//                holder.voiceBtn.setBackgroundResource(R.drawable.sound_max_1_)
//            }
//            "#CCB6B6" -> {
//                holder.starBtn.setBackgroundResource(R.drawable.star_2_)
//                holder.voiceBtn.setBackgroundResource(R.drawable.sound_max_2_)
//            }
//            "#B9B2CB" -> {
//                holder.starBtn.setBackgroundResource(R.drawable.star_3_)
//                holder.voiceBtn.setBackgroundResource(R.drawable.sound_max_3_)
//            }
//        }
//    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setList(itemsList: ArrayList<TopicItem>) {
        this.items = itemsList
    }

    fun getList() : ArrayList<TopicItem>{
        return items
    }

    private fun speakOut(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardFront = view.findViewById<CardView>(R.id.card_front)
        var cardBack = view.findViewById<CardView>(R.id.card_back)

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
