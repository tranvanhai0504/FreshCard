package com.example.freshcard.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.FolderDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.R
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.TopicInfoView
import kotlin.random.Random

class TopicAdapter(var mList: ArrayList<TopicInfoView>, val context: Context): RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.topic_card, parent, false)
        return TopicAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", "undefined")!!
        holder.txtName.text = item.topicName
        holder.txtTotalCards.text = item.totalCards.toString()
        holder.txtTimeAccess.text = "Last access: ${item.timeAccess}"
        holder.txtAccess.text = item.access
        holder.progress.max = 100
        holder.txtProgress.text = "${item.totalLearned}/${item.totalCards} cards learned"
        holder.progress.progress = ((item.totalLearned.toFloat()/item.totalCards.toFloat())*100).toInt()
        val randomNumber = Random.nextInt(0, 4)
        when(randomNumber) {
            0-> {
                holder.wrapper.background =  ColorDrawable(Color.parseColor("#ECE3BA"))
                holder.txtName.setTextColor(Color.parseColor("#D5B04D"))
                holder.txtAccess.setTextColor(Color.parseColor("#D5B04D"))
                holder.img.setImageResource(R.drawable.yimage)
                holder.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.ycorner_progressbar)
            }
            1-> {
                holder.wrapper.background =  ColorDrawable(Color.parseColor("#CCB6B6"))
                holder.txtName.setTextColor(Color.parseColor("#A45353"))
                holder.txtAccess.setTextColor(Color.parseColor("#A45353"))
                holder.img.setImageResource(R.drawable.rimage)
                holder.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.rcorner_progressbar)
            }
            2-> {
                holder.wrapper.background =  ColorDrawable(Color.parseColor("#B9B2CB"))
                holder.txtName.setTextColor(Color.parseColor("#55498D"))
                holder.txtAccess.setTextColor(Color.parseColor("#55498D"))
                holder.img.setImageResource(R.drawable.imgtp1)
                holder.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.pcorner_progressbar)
            }
            3-> {
                holder.wrapper.background =  ColorDrawable(Color.parseColor("#85A5A7"))
                holder.txtName.setTextColor(Color.parseColor("#426B6D"))
                holder.txtAccess.setTextColor(Color.parseColor("#426B6D"))
                holder.img.setImageResource(R.drawable.gimage)
                holder.progress.progressDrawable = ContextCompat.getDrawable(context, R.drawable.gcorner_progressbar)
            }
        }


        if(userId == item.owner) {
            holder.itemView.setOnLongClickListener{
                v->
                var arrNames: ArrayList<String> =  FolderDAO().getFolderNamesShareRef(context)
                var arrIds: ArrayList<String> = FolderDAO().getFolderIdsShareRef(context)

                true
            }
        }

        holder.itemView.setOnClickListener{
            v->
        }


    }

    private fun showDialog(names: ArrayList<String>, ids: ArrayList<String>) {
        val alertDialog = AlertDialog.Builder(context)
        var currId = ""
         alertDialog.setTitle("Choose an Item")
        var checkedItem = -1
        alertDialog.setSingleChoiceItems(names.toTypedArray(),checkedItem) { dialog, which ->
            checkedItem = which
            currId = ids.get(which)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val progress = itemView.findViewById<ProgressBar>(R.id.progressbar)
         val txtName = itemView.findViewById<TextView>(R.id.txtTopicname)
         val txtTotalCards = itemView.findViewById<TextView>(R.id.txtTotalCards)
         val txtProgress  = itemView.findViewById<TextView>(R.id.txtProgress)
         val txtAccess  = itemView.findViewById<TextView>(R.id.txtAccess)
         val txtTimeAccess = itemView.findViewById<TextView>(R.id.txtTimeAccess)
        val wrapper = itemView.findViewById<ConstraintLayout>(R.id.wrapper)
        val img = itemView.findViewById<ImageView>(R.id.imageTp)
    }
}


