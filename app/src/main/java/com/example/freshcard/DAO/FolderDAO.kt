package com.example.freshcard.DAO

import android.content.Context
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.Topic
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FolderDAO() {
    var folderRef: DatabaseReference = Database().getReference("folders")
     fun getNewFolderName(context: Context, saveF: (name: String) -> Unit) {
        val builder = AlertDialog.Builder(context)
        var input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.setPadding(80, 20,20,20)
        builder.setView(input)

        builder.setPositiveButton("Save") { dialog, _ ->
            val folderName = input.text.toString()
            saveF(folderName)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.setTitle("New folder's name")
        dialog.show()
    }


    fun pushFolder(folder: Folder) {
        folderRef.child(folder.id).setValue(folder)
    }


    fun getFolderData(userId: String,myF: (ArrayList<Folder>)-> Unit) {
        var query = folderRef.orderByChild("idUser").equalTo(userId)
        var folders = ArrayList(emptyList<Folder>())
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val id = snapshot.child("id").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    val idUser = snapshot.child("idUser").getValue(String::class.java)
                    var idTopics = snapshot.child("idTopics").getValue()
                    var dataTopics:ArrayList<String>? = idTopics as? ArrayList<String>
                    if(dataTopics == null) {
                        dataTopics = ArrayList(emptyList<String>())
                    }
                    folders.add(Folder(id!!, name!!, dataTopics, idUser!!))
                }
                myF(folders)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }


    fun removeFolder(id: String) {
        folderRef.child(id).removeValue()
    }
}