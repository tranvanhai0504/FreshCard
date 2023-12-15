package com.example.freshcard.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.text.toLowerCase
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.R
import java.util.Locale

class CardResultAdapter(var mList: ArrayList<ArrayList<String>>) : RecyclerView.Adapter<CardResultAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.result_card, parent, false)
        return CardResultAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = mList[position]

        holder.index.text = (position + 1).toString()
        holder.question.text = item[0]
        holder.userAnswer.text = item[1]
        holder.answer.text = item[2]

        if(item[1].equals(item[2], ignoreCase = true)){

            holder.itemView.backgroundTintList =  ColorStateList.valueOf(Color.parseColor("#54B435"))
        }else{
            holder.itemView.backgroundTintList =  ColorStateList.valueOf(Color.parseColor("#E15656"))
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var index = itemView.findViewById<TextView>(R.id.txtIndex)
        var question = itemView.findViewById<TextView>(R.id.txtQuestion)
        var userAnswer = itemView.findViewById<TextView>(R.id.txtUserAnswer)
        var answer = itemView.findViewById<TextView>(R.id.txtAnswer)
    }
}