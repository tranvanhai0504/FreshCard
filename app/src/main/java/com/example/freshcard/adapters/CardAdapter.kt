package com.example.freshcard.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.freshcard.AddTopic
import com.example.freshcard.R
import com.example.freshcard.Structure.TopicItem
import kotlin.random.Random


class CardAdapter(var mList: ArrayList<TopicItem>, val context: Context, val toppicId: String, val upImage: ()->Unit): RecyclerView.Adapter<CardAdapter.ViewHolder>() {
    public var isEditing = false
    public var isCreateNew = false
    private var imagesMap = hashMapOf<String, Uri>()
    private var currImageName = ""
    val origin = context as Activity
    lateinit var  currHolder: CardAdapter.ViewHolder
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
        holder.txtDes.setTextColor(Color.parseColor(textColors.get(randomNumber)))
        holder.txtVocab.setText(item.en)
        holder.txtVietnamese.setText(item.vie)
        holder.txtDes.setText(item.description)
        holder.txtVocab.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bacColors.get(randomNumber)))
        holder.txtVietnamese.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bacColors.get(randomNumber)))
        holder.txtDes.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bacColors.get(randomNumber)))
        holder.btnUploadImage.setOnClickListener{
            v->
            upImage()
        }
        holder.btnOption.setOnClickListener{
            v->
            showPopupMenu(holder)
        }
        holder.btnSave.setOnClickListener{
            v->
            handleSave()
            disableEdit(holder)
        }

        if(holder.txtVocab.text.toString() == "") {
            currHolder = holder
            enableEdit(holder)

        }
        if(item.image == "") {
            holder.image.isVisible = false
            isCreateNew = true
        }else{
            if(imagesMap.containsKey(item.id)) {
                holder.image.setImageURI(imagesMap.get(item.id))
                holder.image.isVisible = true
            }
        }

        Log.e("hashmap", "${imagesMap}")

        holder.btnCancle.setOnClickListener{
                v->
            if(!isCreateNew) {
                holder.txtVocab.setText(item.en)
                holder.txtVietnamese.setText(item.vie)
            }else {
                mList.removeAt(position)
                notifyDataSetChanged()
            }
            disableEdit(holder)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
    private fun showPopupMenu(holder: CardAdapter.ViewHolder) {
        val popupMenu = PopupMenu(context, holder.btnOption)
        popupMenu.inflate(R.menu.card_popup_menu) // Inflate your menu resource

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.popupBtnDelete -> {
                    currHolder = holder
                    confirmDelete()
                    true
                }
                R.id.popupBtnEdit -> {
                    if(isEditing) {
                        confirmChange()
                    }else {
                        isCreateNew = false
                        currHolder = holder
                        enableEdit(holder)
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

    public fun createEmptyCard() {
        mList.add(0,TopicItem("${toppicId}<${mList.size}","", "", "", ""))
        notifyDataSetChanged()
    }

    fun enableEdit(holder: CardAdapter.ViewHolder) {
        holder.txtVietnamese.isEnabled = true
        holder.txtVocab.isEnabled = true
        holder.btnOption.isVisible = false
        holder.btnCancle.isVisible = true
        holder.btnSave.isVisible = true
        holder.btnUploadImage.isVisible = true
        holder.txtDes.isEnabled = true
        isEditing = true
        holder.txtVocab.requestFocus()
    }

    fun updateData(list: ArrayList<TopicItem>) {
        mList = list
        notifyDataSetChanged()
    }

    fun disableEdit(holder: CardAdapter.ViewHolder) {
        holder.txtVietnamese.isEnabled = false
        holder.txtVocab.isEnabled = false
        holder.btnOption.isVisible = true
        holder.btnCancle.isVisible = false
        holder.btnSave.isVisible = false
        holder.btnUploadImage.isVisible = false
        holder.txtDes.isEnabled = false
        holder.txtVocab.clearFocus()
        holder.txtVietnamese.clearFocus()
        isEditing = false
    }

    fun handleSave() {
        var topicItem = TopicItem("${mList[currHolder.absoluteAdapterPosition].id}", currHolder.txtVocab.text.toString(), currHolder.txtVietnamese.text.toString(), currHolder.txtDes.text.toString(), currImageName)
        if(!isCreateNew) {
            topicItem = mList[currHolder.absoluteAdapterPosition]
            topicItem.en =  currHolder.txtVocab.text.toString()
            topicItem.vie =  currHolder.txtVietnamese.text.toString()
            topicItem.description =  currHolder.txtDes.text.toString()
            topicItem.image = currImageName
        }
        currImageName = ""
        mList.removeAt(currHolder.absoluteAdapterPosition)
        mList.add(currHolder.absoluteAdapterPosition,topicItem)
        disableEdit(currHolder)
        AddTopic().adapterData = mList
    }

    private fun handleRemove() {
        mList.removeAt(currHolder.absoluteAdapterPosition)
        AddTopic().adapterData = mList
        notifyDataSetChanged()
    }

    public fun confirmChange() {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setMessage("Save change?")
            .setTitle("Alert")
            .setPositiveButton("YES") {
                    dialog, which ->
                    handleSave()

            }
            .setNegativeButton("NO") {
                    dialog, which ->
                    if(isCreateNew) {
                        mList.removeAt(currHolder.absoluteAdapterPosition)
                        isCreateNew = false
                        notifyDataSetChanged()
                    }else{
                        val item = mList[currHolder.absoluteAdapterPosition]
                        currHolder.txtVietnamese.setText(item.vie)
                        currHolder.txtVocab.setText(item.en)
                        disableEdit(currHolder)
                    }
                isEditing = false


            }
        val dialog: AlertDialog = builder.create()
        dialog.show()

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



    fun setImage(imageUri: Uri, name: String) {
        currHolder.image.setImageURI(imageUri)
        currImageName = name
        currHolder.image.isVisible = true
        imagesMap.set(mList[currHolder.absoluteAdapterPosition].id, imageUri)
        Log.e("hashmap", (mList[currHolder.absoluteAdapterPosition].id))
    }





    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtVocab: EditText = itemView.findViewById(R.id.txtVocab)
        val line: TextView = itemView.findViewById(R.id.line)
        val txtVietnamese: EditText = itemView.findViewById(R.id.txtVietnamese)
        val txtDes: EditText = itemView.findViewById(R.id.txtDes)
        val btnOption: ImageButton = itemView.findViewById(R.id.btnOption)
        val image: ImageView = itemView.findViewById(R.id.image)
        val btnCancle: ImageButton =  itemView.findViewById(R.id.btnCancle)
        val btnSave: ImageButton =  itemView.findViewById(R.id.btnSave)
        val btnUploadImage: Button = itemView.findViewById(R.id.btnUploadImage)
    }
}