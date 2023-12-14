package com.example.freshcard.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.R
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.RankingItemView

class RankingAdapter(var mList: ArrayList<RankingItemView>, val context: Context): RecyclerView.Adapter<RankingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_topic_card_view, parent, false)
        return RankingAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RankingAdapter.ViewHolder, position: Int) {
        var item  = mList[position]
        holder.topicName.text = item.topicName
        holder.txtOwner.text = "Owner: ${item.owner}"
        holder.txtFirstPlaceName.text = item.firstPlaceUser.fullName
        holder.txtSecPlaceName.text = item.secPlaceUser.fullName
        holder.txtThirdPlaceName.text = item.thirPlaceUser.fullName
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var topicName: TextView = itemView.findViewById(R.id.txtTopicName)
        var txtOwner: TextView = itemView.findViewById(R.id.txtOwner)
        var txtFirstPlaceName: TextView = itemView.findViewById(R.id.txtFirstPlaceName)
        var txtSecPlaceName: TextView = itemView.findViewById(R.id.txtSecPlaceName)
        var txtThirdPlaceName: TextView = itemView.findViewById(R.id.txtThirdPlaceName)
        var imgFirst: ImageView = itemView.findViewById(R.id.imgTop1)
        var imgSec: ImageView = itemView.findViewById(R.id.imgTop2)
        var imgthird: ImageView = itemView.findViewById(R.id.imgTop3)
    }
}