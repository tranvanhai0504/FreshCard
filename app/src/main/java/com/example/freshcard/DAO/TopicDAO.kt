package com.example.freshcard.DAO

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Topic
import com.example.freshcard.adapters.TopicNewHomeAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue

public class TopicDAO() {
    var topicRef: DatabaseReference = Database().getReference("topics")

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
}