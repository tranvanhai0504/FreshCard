package com.example.freshcard.adapters

import android.content.Context
import android.content.Intent
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
import androidx.appcompat.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.FolderDAO
import com.example.freshcard.DAO.HistoryDAO
import com.example.freshcard.DAO.TopicDAO
import com.example.freshcard.FlashCardLearnActivity
import com.example.freshcard.FolderViewActivity
import com.example.freshcard.R
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicInfoView
import kotlin.random.Random

class TopicAdapter(var mList: ArrayList<TopicInfoView>, val context: Context, val ct: String): RecyclerView.Adapter<TopicAdapter.ViewHolder>() {
   private lateinit var currHolder: ViewHolder
   private var folders: ArrayList<Folder> = ArrayList(emptyList<Folder>())
    var arrNames: ArrayList<String> =  ArrayList(emptyList<String>())
    var arrIds: ArrayList<String> =  ArrayList(emptyList<String>())
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
        Log.e("result", "1set time")
        HistoryDAO().getLastAccess(item.owner,item.topicId) {
            holder.txtTimeAccess.text = it.substring(IntRange(0, 19))
            Log.e("result", "set time")
        }
        holder.progress.max = 100
        holder.txtProgress.text = "${item.totalLearned}/${item.totalCards} cards learned"
        holder.progress.progress = ((item.totalLearned.toFloat()/item.totalCards.toFloat())*100).toInt()
        if(item.access) {
            holder.txtAccess.text = "Public"
        }else {
            holder.txtAccess.text = "Private"
        }
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

        FolderDAO().getFolderData(item.owner) {
            arrIds.clear()
            arrNames.clear()
            for(item in it) {
                arrIds.add(item.id)
                arrNames.add(item.name)
            }
        }

        if(userId == item.owner) {
            holder.itemView.setOnLongClickListener{
                v->
                currHolder = holder

                showPopupMenu(holder, arrNames, arrIds)
                true
            }
        }

        holder.itemView.setOnClickListener{
            v->
                var intent = Intent(context, FlashCardLearnActivity::class.java)
                intent.putExtra("idTopic", item.topicId)
                context.startActivity(intent)
        }
    }

    fun setList(arr : ArrayList<TopicInfoView>) {
        mList = arr
        notifyDataSetChanged()
    }

    fun clearList() {
        mList.clear()
        Log.e("clear", "clear")
        notifyDataSetChanged()
    }

    fun addTopic(topic: TopicInfoView) {
        mList.add(0,topic)
        notifyDataSetChanged()
    }

    private fun showDialog(names: ArrayList<String>, ids: ArrayList<String>) {
        val alertDialog = AlertDialog.Builder(context)
        var currId = ""
        var item = mList[currHolder.position]
         alertDialog.setTitle("Choose a folder")
        var checkedItem = -1

        Log.e("topic", "x${names}, ---${ids}")
        alertDialog.setSingleChoiceItems(names.toTypedArray(),checkedItem) { dialog, which ->
            checkedItem = which
            currId = ids.get(which)
            FolderDAO().addTopic(currId, item.topicId)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }


    private fun showPopupMenu(holder: TopicAdapter.ViewHolder, arrNames: ArrayList<String> ,arrIds: ArrayList<String> ) {
        val popupMenu = PopupMenu(context, holder.txtAccess)
       if(ct == "view") {
           popupMenu.inflate(R.menu.topic_context_menu) // Inflate your menu resource
       }else {
           popupMenu.inflate(R.menu.folder_view_context_menu) // Inflate your menu resource
       }
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.topicContextMenuRemove -> {
                    currHolder = holder
                    confirmDelete()
                    true
                }
                R.id.topicContextMenuAddToFolder -> {
                    showDialog(arrNames,arrIds)
                    true
                }
                R.id.removeFromFolder-> {
                    confirmRemoveFromFolder()
                    true
                }
                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun removeTopicFromList(id: String) {
        mList = ArrayList(mList.filter { it.topicId!=id })
        notifyDataSetChanged()
    }

    fun handleRemove(id: String) {
        TopicDAO().removeTopic(id)
    }

    private fun confirmDelete() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var item = mList[currHolder.position]

        builder.setMessage("Delete topic ${item.topicName}?")
            .setTitle("Alert")
            .setPositiveButton("YES") {
                    dialog, which ->
                    handleRemove(item.topicId)
            }
            .setNegativeButton("NO") {
                    dialog, which ->
                //no fun
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                v->
            dialog.cancel()
        }
    }

    private fun confirmRemoveFromFolder() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        var item = mList[currHolder.position]

        builder.setMessage("Remove ${item.topicName} from folder ?")
            .setTitle("Remove topic")
            .setPositiveButton("YES") {
                    dialog, which ->

                    FolderViewActivity().removeTopicFromFolder(item.topicId, context.applicationContext)
                    removeTopicFromList(item.topicId)
            }
            .setNegativeButton("NO") {
                    dialog, which ->
            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                v->
            dialog.cancel()
        }
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


