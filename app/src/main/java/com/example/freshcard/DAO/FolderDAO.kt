package com.example.freshcard.DAO

import android.content.Context
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.freshcard.MainActivity
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class FolderDAO() {
    private var currFolders = ArrayList(emptyList<Folder>())
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
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                folders = ArrayList(emptyList<Folder>())
                Log.e("topic", "reload")
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
                MainActivity().getContext {
                }
                currFolders = folders
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

    fun setFoldersShareRef(context: Context, data: ArrayList<Folder>) {
        if(context!=null) {
            val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
            var editor = sharedPreferences.edit()
            var set = mutableSetOf<String>()
            var idSet = mutableSetOf<String>()
            for(item in data) {

                set.add(item.name)
                idSet.add(item.id)
            }
            editor.putStringSet("folderNamesSet", set)
            editor.putStringSet("folderIdsSet", idSet)
            Log.e("topiccx", "${idSet}-----${set}")
            editor.apply()
        }
    }

    fun addTopic(folderId: String, topicId:String) {
        folderRef.child(folderId).child("idTopics").get().addOnSuccessListener {
            var idTopics: ArrayList<String>? = ArrayList(emptyList<String>())
            if(it.getValue()!=null) {
                var list = it.getValue()
                idTopics = list  as? ArrayList<String>
            }
            idTopics!!.add(topicId)
            folderRef.child(folderId).child("idTopics").setValue(idTopics)
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }


    fun getFolderNamesShareRef(context: Context): ArrayList<String> {
        val sharedPreferences = context.applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val set = sharedPreferences.getStringSet("folderNamesSet", emptySet())!!
        return set!!.toCollection(ArrayList())
    }

    fun getFolderIdsShareRef(context: Context): ArrayList<String> {
        val sharedPreferences = context.applicationContext.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val set = sharedPreferences.getStringSet("folderIdsSet", emptySet())
        return set!!.toCollection(ArrayList())
    }

//    fun getFoldersShareRef(myF: (ArrayList<Folder>)-> Unit) {
//        var query = folderRef.orderByChild("idUser").equalTo(userId)
//        var folders = ArrayList(emptyList<Folder>())
//        query.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                folders = ArrayList(emptyList<Folder>())
//                Log.e("topic", "reload")
//                for (snapshot in dataSnapshot.children) {
//                    val id = snapshot.child("id").getValue(String::class.java)
//                    val name = snapshot.child("name").getValue(String::class.java)
//                    val idUser = snapshot.child("idUser").getValue(String::class.java)
//                    var idTopics = snapshot.child("idTopics").getValue()
//                    var dataTopics:ArrayList<String>? = idTopics as? ArrayList<String>
//                    if(dataTopics == null) {
//                        dataTopics = ArrayList(emptyList<String>())
//                    }
//                    folders.add(Folder(id!!, name!!, dataTopics, idUser!!))
//                }
//                MainActivity().getContext {
//                }
//                currFolders = folders
//                myF(folders)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle errors
//            }
//        })
//    }

}