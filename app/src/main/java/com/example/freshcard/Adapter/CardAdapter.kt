package com.example.freshcard.Adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.R
import com.example.freshcard.Structure.TopicItem
import kotlin.random.Random

class CardAdapter(var mList: ArrayList<TopicItem>, val context: Context): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.ViewHolder, position: Int) {
        var item = mList[position]

        val randomNumber = Random.nextInt(0, 3)
        var bacColors = listOf<String>("#ece3ba","#ccb6b6", "#b9b2cb", "#85a5a7")
        val textColors = listOf<String>("#d5b04d", "#a45353", "#55498d", "#426b6d")
        holder.itemView.backgroundTintList =  ColorStateList.valueOf(Color.parseColor(bacColors.get(randomNumber)))
        holder.btnOption.backgroundTintList =  ColorStateList.valueOf(Color.parseColor(bacColors.get(randomNumber)))
        holder.line.background = ColorDrawable(Color.parseColor(textColors.get(randomNumber)))
        holder.txtVocab.setTextColor(Color.parseColor(textColors.get(randomNumber)))
        holder.txtVietnamese.setTextColor(Color.parseColor(textColors.get(randomNumber)))
        holder.txtVocab.text = item.en
        holder.txtVietnamese.text = item.vie
        holder.btnOption.setOnClickListener{
            v->
            showPopupMenu(holder.itemView)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.card_popup_menu) // Inflate your menu resource

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popupBtnDelete -> {
//                    confirmDelete(studentId)
                    true
                }
                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtVocab: TextView = itemView.findViewById(R.id.txtVocab)
        val line: TextView = itemView.findViewById(R.id.line)
        val txtVietnamese: TextView = itemView.findViewById(R.id.txtVietnamese)
        val btnOption: ImageButton = itemView.findViewById(R.id.btnOption)
    }
}