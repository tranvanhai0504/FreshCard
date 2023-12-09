package com.example.freshcard.DAO


import android.provider.ContactsContract.Data
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.MainActivity
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.Structure.User
import com.google.android.gms.tasks.Tasks
import com.example.freshcard.adapters.TopicNewHomeAdapter
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
    var learningTopicRef: DatabaseReference = Database().getReference("learningtopics")

    fun getNewTopics(list : ArrayList<Topic>, adapter: TopicNewHomeAdapter){
        val query = topicRef.orderByChild("public").equalTo(true).limitToFirst(10)

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                for (topicSnapshot in snapshot.children) {
                    var topic: Topic? = topicSnapshot.getValue<Topic>()
                    if (topic != null) {
                        list.add(topic)
                    };
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        query.addValueEventListener(valueEventListener)
    }
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
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.i("log", "hhhh")
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
                    var learningTopic = MainActivity.Companion.user.learningTopics
                    Log.i("learning", learningTopic?.size.toString() + learningTopic.toString())
                    learningTopic?.forEach { it ->
                        if(it.idTopic == topicId){
                            var newTopicView = TopicInfoView(it.idTopic, snapshot.child("title").getValue(String::class.java)!!,
                                items.size , it.idLearned.size,"",
                                snapshot.child("public").value.toString(), owner)
                            Log.e("topicx", "${newTopicView}")
                            topicInfoList.add(newTopicView)
                            myF(topicInfoList)
                        }
                    }
//                    getTopicById(topicId!!) {tp ->
//                        UserDAO().getLearnedInfoByUser(owner, tp.id) {size ->
//                            var newTopicView =
//                                tp.items?.let {
//                                    TopicInfoView(tp.id,tp.title,
//                                        it.size,size,"", tp.isPublic.toString(), owner)
//                                }
//                            Log.e("topicx", "${newTopicView}")
//                            topicInfoList.add(newTopicView)
//                            myF(topicInfoList)
//                        }
//                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }



    fun pushLearningTopic(item: LearningTopic) {
        learningTopicRef.child("${item.idTopic}").setValue(item)
    }

    fun removeTopic(id: String) {
        topicRef.child(id).removeValue()
    }

}
