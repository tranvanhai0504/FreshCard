package com.example.freshcard.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.ImageDAO
import com.example.freshcard.DAO.UserDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.RankingItemView
import com.example.freshcard.Structure.UserRankingItem

class UserRankingAdapter(var mList: ArrayList<UserRankingItem>, val context: Context): RecyclerView.Adapter<UserRankingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserRankingAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_user_card, parent, false)
        return UserRankingAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = mList[position]
        holder.txtname.text = item.name
        holder.txtPerforment.text = item.performent
        holder.txtIndex.text = "${position + 1}"
        if(position>2) {
            holder.img.visibility = View.INVISIBLE
        }else {
            holder.txtname.setTextColor(Color.parseColor("#D5B04D"))
            holder.txtIndex.setTextColor(Color.parseColor("#D5B04D"))
            holder.constraintLayout.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ECE3BA"))
            //set text color to
        }
    }



    override fun getItemCount(): Int {
        return mList.size
    }

    fun addItem(item : UserRankingItem) {
        mList.add(item)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtname = itemView.findViewById<TextView>(R.id.txtFullname)
        val txtIndex = itemView.findViewById<TextView>(R.id.txtIntex)
        val txtPerforment = itemView.findViewById<TextView>(R.id.txtPerforment)
        val img = itemView.findViewById<ImageView>(R.id.icon)
        val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.constraintLayout)
    }
}