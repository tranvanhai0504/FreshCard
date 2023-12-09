package com.example.freshcard.DAO


import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.MainActivity
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.LearningTopic
import com.example.freshcard.Structure.Topic
import com.example.freshcard.Structure.TopicItem
import com.example.freshcard.Structure.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneOffset

public class UserDAO() {
    var db: DatabaseReference = Database().getReference("users")
    var topicRef: DatabaseReference = Database().getReference("topics")

    fun login(email: String, password: String, onResult: (HashMap<String, Any?>?) -> Unit) {
        val query = db.orderByChild("email").equalTo(email)

        val valueEventListener = object : ValueEventListener {
            var result = hashMapOf<String, Any?>()

            override fun onDataChange(snapshot: DataSnapshot) {

                result["state"] = true

                for (userSnapshot in snapshot.children) {
                    val hashPass = userSnapshot.child("password").value.toString()

                    //check password
                    val isMatchPassword =
                        BCrypt.verifyer().verify(password.toCharArray(), hashPass.toCharArray())
                    if (!isMatchPassword.verified) {
                        result["state"] = false
                        result["message"] = "Password is not correct"
                        onResult(result)
                        return
                    }

                    //login successful
                    val userId = userSnapshot.key
                    result["message"] = userId

                    onResult(result)
                    return
                }

                //message user not exist
                result["state"] = false
                result["message"] = "Email is not exist!"

                onResult(result)
            }

            override fun onCancelled(error: DatabaseError) {
                result["state"] = false
                result["message"] = "Can't find account!"

                onResult(result)
            }
        }
        query.addListenerForSingleValueEvent(valueEventListener)
    }

    fun register(email: String, password: String, name: String): Boolean {
        val key = db.push().key;

        if (key != null) {
            var newUser = User(password, name, "", email, "", null, 0, null)
            db.child(key).setValue(newUser)
            return true;
        } else {
            return false;
        }
    }

    suspend fun getUserInfor(id : String) : DataSnapshot{
        val result = db.child(id).get().await()
        return result
    }

    fun getDbUser(): DatabaseReference {
        return this.db
    }

    fun pushTopic(topic: Topic) {
        topicRef.child(topic.id).setValue(topic)
    }

    fun getTopicById(id: String) : Topic{
        var topic = Topic("","", "", ArrayList(emptyList<TopicItem>()), false, ArrayList(emptyList()), 0, 0)
        topicRef.child("topics").child(id).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.child("title")}")
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        return topic!!
    }

    fun bookmarkTopic(idUser : String, idTopic : String, isAdded : Boolean){
        if(isAdded){
            val bookmarkMap = HashMap<String, Boolean>()
            bookmarkMap[idTopic] = true
            db.child(idUser).child("bookmarkedTopics").updateChildren(bookmarkMap as Map<String, Any>)
        }else{
            db.child(idUser).child("bookmarkedTopics").child(idTopic).removeValue()
        }
    }

    fun addLearningTopic(idUser : String, idTopic: String){
        if(MainActivity.Companion.user.learningTopics?.find {
            it.idTopic == idTopic
            } != null){
            return
        }

        var learningTopic = LearningTopic(idTopic)
        topicRef.child(idTopic).get().addOnSuccessListener {
            var topicItem = it.child("items")

            var gti = object : GenericTypeIndicator<ArrayList<TopicItem>>(){}
            var topicItems = topicItem.getValue(gti)
            if (topicItems != null) {
                learningTopic.idLearning = topicItems.map { it -> it.id } as ArrayList<String>
            }

            MainActivity.Companion.user.learningTopics?.add(learningTopic)
            db.child(idUser).child("learningTopics").setValue(MainActivity.Companion.user.learningTopics)
        }
    }

    fun UpdateDateLogin(id: String){
        db.child(id).child("lastAccess").setValue(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
    }
}
