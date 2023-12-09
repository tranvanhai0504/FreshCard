package com.example.freshcard.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.MainActivity
import com.example.freshcard.R
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.User
import com.example.freshcard.fragments.NewTopicFragment
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.example.freshcard.FlashCardLearnActivity


class TopicNewHomeAdapter(var mList: ArrayList<Topic>, val context: NewTopicFragment): RecyclerView.Adapter<TopicNewHomeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_new_topic, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = mList[position]
        Log.i("item", item.toString())

        val randomNumber = Random.nextInt(0, 3)
        var bacColors = listOf<String>("#ece3ba","#ccb6b6", "#b9b2cb", "#85a5a7")
        val textColors = listOf<String>("#d5b04d", "#a45353", "#55498d", "#426b6d")

        holder.txtTopicName.text = item.title
        holder.txtNumberCard.text = item.items?.size.toString() + " cards"
        holder.txtPeopleLearned.text = item.learnedPeople?.size.toString() + " learned"

        var user : DataSnapshot
        runBlocking {
            user = UserDAO().getUserInfor(item.owner)
        }

        holder.txtOwnerName.text = user.child("email").value.toString()
        ImageDAO().getImage(user.child("avatar").value.toString(), holder.imageAvatar, "avatars")

        var mainColor = textColors[position % textColors.size]
        var subColor = bacColors[position % bacColors.size]

        changeColor(holder, mainColor, subColor)

        //add margin to last item
        if(position == itemCount - 1){
            val params = holder.itemView.layoutParams as RecyclerView.LayoutParams
            params.bottomMargin = 40 // last item bottom margin
            holder.itemView.layoutParams = params
        }

        //get list of bookmark in current user
        val listBookmark = MainActivity.Companion.user.bookmarkedTopics?.keys
        var isBookmarked = false

        listBookmark?.find {
            it == item.id
        }?.let {
            isBookmarked = true
            changeBookmarkColor(holder, mainColor)
        }

        val sharedPreferences = context.activity?.applicationContext?.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        var userId = sharedPreferences?.getString("idUser", "undefined")!!

        holder.btnBookMark.setOnClickListener {
            UserDAO().bookmarkTopic(userId, item.id, !isBookmarked)
            if(!isBookmarked){
                MainActivity.Companion.user.bookmarkedTopics?.set(item.id, true)
            }else{
                MainActivity.Companion.user.bookmarkedTopics?.remove(item.id)
            }
            notifyDataSetChanged()
        }

        //set click of item
        holder.itemView.setOnClickListener {
            var intent = Intent(context.requireContext(), FlashCardLearnActivity::class.java)
            intent.putExtra("idTopic", item.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun changeBookmarkColor(holder: ViewHolder, color: String){
        val bookmarkDrawable = ContextCompat.getDrawable(context.requireContext(), R.drawable.bookmark_fill)
        val wrappedBookmarkDrawable = DrawableCompat.wrap(bookmarkDrawable!!.mutate())
        DrawableCompat.setTint(wrappedBookmarkDrawable, Color.parseColor(color))
        holder.btnBookMark.background = wrappedBookmarkDrawable
    }

    fun changeColor(holder: ViewHolder, mainColor : String, subColor : String){
        holder.txtTopicName.setTextColor(Color.parseColor(mainColor))
        holder.txtOwnerName.setTextColor(Color.parseColor(mainColor))
        holder.txtPeopleLearned.setTextColor(Color.parseColor(mainColor))
        holder.itemView.backgroundTintList =  ColorStateList.valueOf(Color.parseColor(subColor))

        when(mainColor){
            "#d5b04d" -> {
                holder.btnBookMark.setBackgroundResource(R.drawable.bookmark_fill)
            }
            "#a45353" -> {
                holder.btnBookMark.setBackgroundResource(R.drawable.bookmark_fill_3_)
            }
            "#55498d" -> {
                holder.btnBookMark.setBackgroundResource(R.drawable.bookmark_fill_1_)
            }
            "#426b6d" -> {
                holder.btnBookMark.setBackgroundResource(R.drawable.bookmark_fill_2_)
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtTopicName: TextView = itemView.findViewById(R.id.txtNameTopic)
        val txtNumberCard : TextView = itemView.findViewById(R.id.txtNumberCard)
        val imageAvatar : ImageView = itemView.findViewById(R.id.imageView)
        val txtOwnerName : TextView = itemView.findViewById(R.id.txtOwnerName)
        val txtPeopleLearned : TextView = itemView.findViewById(R.id.txtPeopleLearned)
        val btnBookMark : ImageButton = itemView.findViewById(R.id.btnBookMark)
    }
}