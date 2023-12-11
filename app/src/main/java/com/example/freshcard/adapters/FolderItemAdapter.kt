package com.example.freshcard.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.DAO.FolderDAO
import com.example.freshcard.FolderViewActivity
import com.example.freshcard.R
import com.example.freshcard.Structure.Folder
import java.util.Calendar
import kotlin.random.Random

class FolderItemAdapter(var mList: ArrayList<Folder>, val context: Context): RecyclerView.Adapter<FolderItemAdapter.ViewHolder>() {
private lateinit var currHolder:  FolderItemAdapter.ViewHolder
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FolderItemAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.folder_item_card, parent, false)
        return FolderItemAdapter.ViewHolder(view)
    }
    override fun onBindViewHolder(holder: FolderItemAdapter.ViewHolder, position: Int) {
        val item = mList[position]
        val randomNumber = Random.nextInt(0, 4)
        holder.txtCateName.text = item.name
        holder.txtTotalTopics.text = item.idTopics.size.toString()
        when(randomNumber) {
            0-> {
                holder.imgIcon.setImageResource(R.drawable.bfodler)
            }
            1->{
                holder.imgIcon.setImageResource(R.drawable.yfolder)
            }
            2-> {
                holder.imgIcon.setImageResource(R.drawable.pfolder)
            }
            3->{
                holder.imgIcon.setImageResource(R.drawable.rfolder)
            }
            4 -> {
                holder.imgIcon.setImageResource(R.drawable.gfolder)
            }
        }

        holder.btnOption.setOnClickListener{
            v->
            currHolder = holder
            Log.e("adapter", "setholder: ${holder}, \n${currHolder}")
            showPopupMenu(holder)
        }

        holder.itemView.setOnClickListener {
            var intent = Intent(context, FolderViewActivity::class.java)
            intent.putExtra("folderId", item.id)
            intent.putExtra("folder", item)
            val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)

            val editor = sharedPreferences.edit()
            editor.putString("folderId", item.id)
            editor.apply()
            Log.e("remove", "${sharedPreferences.all}")
            context.startActivity(intent)
        }

    }

    fun createNewfolder(name: String) {
        val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", "undefined")!!
        val newFolder =  Folder("${userId}${getCurrentTimeInDecimal()}", name, ArrayList(emptyList<String>()), userId)
        mList.add(0,newFolder)
        FolderDAO().pushFolder(newFolder)
        notifyDataSetChanged()
    }

    fun getCurrentTimeInDecimal(): Int {
        val calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY) * 10000
        val minutes = calendar.get(Calendar.MINUTE) * 100
        val seconds = calendar.get(Calendar.SECOND)
        val timeInteger = hours + minutes + seconds
        return timeInteger
    }

    private fun showPopupMenu(holder: FolderItemAdapter.ViewHolder) {
        val popupMenu = PopupMenu(context, holder.btnOption)
        var currFolder = mList[holder.position]
        popupMenu.inflate(R.menu.folder_popup_menu) // Inflate your menu resource
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.folderPopupRemove -> {
                    currHolder = holder
                    confirmDelete()
                    true
                }
                R.id.folderPopupEdit -> {
                    FolderDAO().getNewFolderName(context){ name->
                        handleEdit(name)
                        FolderDAO().updateFolderTitle(currFolder.id, name)
                        }
                    true
                }
                else -> {
                    true
                }
            }
        }

        popupMenu.show()
    }

    private fun handleEdit(name: String) {
        var index = currHolder.position
        Log.e("adapter", "${currHolder}")

        var item = mList[index]
        item.name = name
        mList.removeAt(index)
        mList.add(index, item)
        notifyDataSetChanged()
    }

    public fun confirmDelete() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Delete this card?")
            .setTitle("Alert")
            .setPositiveButton("YES") {
                    dialog, which ->
                        handleRemove()
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

    private fun handleRemove() {
        var item = mList[currHolder.absoluteAdapterPosition]
        mList.removeAt(currHolder.absoluteAdapterPosition)
        FolderDAO().removeFolder(item.id)
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imgIcon = itemView.findViewById<ImageView>(R.id.imgFolder)
        val txtCateName = itemView.findViewById<TextView>(R.id.txtCategoryName)
        val txtTotalTopics = itemView.findViewById<TextView>(R.id.txtTotalTopic)
        val btnOption = itemView.findViewById<ImageButton>(R.id.btnOption)
    }
}