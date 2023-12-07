package com.example.freshcard.adapters

import android.graphics.BitmapFactory
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
import com.example.freshcard.R
import com.example.freshcard.Structure.Topic
import com.example.freshcard.fragments.NewTopicFragment
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URL
import kotlin.random.Random


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
        holder.txtNumberCard.text = item.items.size.toString() + " cards"
        holder.txtOwnerName.text = "hai"
        holder.txtPeopleLearned.text = item.learnedPeople.size.toString() + " learned"

        var user : DataSnapshot
        runBlocking {
            user = UserDAO().getUserInfor(item.owner)
        }

        holder.txtOwnerName.text = user.child("email").value.toString()

//        val newurl = URL(user.child("avatar").value.toString())
//        val mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream())
//        holder.imageAvatar.setImageBitmap(mIcon_val)
        ImageDAO().getImage(user.child("avatar").value.toString(), holder.imageAvatar, "avatars")
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getOwnerInfor(id : String){

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