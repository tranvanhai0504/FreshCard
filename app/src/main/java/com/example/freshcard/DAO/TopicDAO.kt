package com.example.freshcard.DAO


import android.provider.ContactsContract.Data
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.Structure.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

public class TopicDAO() {
    var topicRef: DatabaseReference = Database().getReference("topics")
    fun getTopicById(id: String, myF: (Topic)-> Unit) : Topic{
        var topic = Topic("","", "", ArrayList(emptyList<TopicItem>()), false, ArrayList(emptyList()))
        topicRef.child(id).get().addOnSuccessListener {
            if(it.getValue()!=null) {
                Log.e("topicc", "${it.getValue()}")
                var id = it.child("id").getValue(String::class.java)
                var title = it.child("title").getValue(String::class.java)
                var isPublic = it.child("public").getValue(Boolean::class.java)
                var lsStr = it.child("learnedPeople").getValue()
                var learnedPeople: ArrayList<String>? = lsStr as? ArrayList<String>
                if(learnedPeople == null) {
                    learnedPeople = ArrayList(emptyList<String>())
                }
                var items = ArrayList<TopicItem>()
                var owner = it.child("owner").getValue(String::class.java)
                for(itemSnapshot in it.child("items").children) {
                    var id = itemSnapshot.child("id").getValue(String::class.java)
                    var en = itemSnapshot.child("en").getValue(String::class.java)
                    var vie = itemSnapshot.child("vie").getValue(String::class.java)
                    var description = itemSnapshot.child("description").getValue(String::class.java)
                    var image = itemSnapshot.child("image").getValue(String::class.java)
                    var newItem = TopicItem(id!!, en!!, vie!!, description!!, image!!)
                    items.add(newItem)
                }
                topic = Topic(id!!, owner!!,title!!, items, isPublic!!, learnedPeople!! )
                myF(topic!!)
                Log.e("topicxx", "${topic}")
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        return topic!!
    }

    fun getTopicInfoViewByOwner(owner: String, myF: (ArrayList<TopicInfoView>)-> Unit) {
        val query = topicRef.orderByChild("owner").equalTo(owner)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var topicInfoList = ArrayList(emptyList<TopicInfoView>())
                for (snapshot in dataSnapshot.children) {
                    var items = ArrayList<TopicItem>()
                    var topicId = snapshot.child("id").getValue(String::class.java)
                    for(itemSnapshot in snapshot.child("items").children) {
                        var id = itemSnapshot.child("id").getValue(String::class.java)
                        var en = itemSnapshot.child("en").getValue(String::class.java)
                        var vie = itemSnapshot.child("vie").getValue(String::class.java)
                        var description = itemSnapshot.child("description").getValue(String::class.java)
                        var image = itemSnapshot.child("image").getValue(String::class.java)
                        var newItem = TopicItem(id!!, en!!, vie!!, description!!, image!!)
                        items.add(newItem)
                    }
                    Log.e("topic", "${items}")
                    getTopicById(topicId!!) {tp ->
                        UserDAO().getLearnedInfoByUser(owner, tp.id) {size ->
                            var newTopicView = TopicInfoView(tp.id,tp.title,tp.items.size,size,"", tp.isPublic.toString(), owner)
                            Log.e("topicx", "${newTopicView}")
                            topicInfoList.add(newTopicView)
                            myF(topicInfoList)
                        }
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
}
