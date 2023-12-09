package com.example.freshcard.DAO


import android.content.Context
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.freshcard.Structure.Database
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
        query.addValueEventListener(valueEventListener)

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

    fun pushLearningTopic(learningTopic: LearningTopic, usId: String) {
        db.child(usId).get().addOnSuccessListener {
            var items = it.child("learningTopics")
            var learningTopics  = ArrayList(emptyList<LearningTopic>())
            if(items!=null) {
                for( item in items.children) {
                    var idTopic = item.child("idTopic").getValue(String::class.java)
                    var idLearned1 = item.child("idLearned").getValue()
                    var idLearned:ArrayList<String>? = idLearned1 as? ArrayList<String>
                    var idLearning1 = item.child("idLearning").getValue()
                    var idLearning:ArrayList<String>? = idLearning1  as? ArrayList<String>
                    var idChecked1 = item.child("idChecked").getValue()
                    var idChecked:ArrayList<String>?  = idChecked1  as? ArrayList<String>
                    if(idLearned==null) {
                        idLearned = ArrayList(emptyList<String>())
                    }
                    if(idLearning==null) {
                        idLearning = ArrayList(emptyList<String>())
                    }
                    if(idChecked==null) {
                        idChecked = ArrayList(emptyList<String>())
                    }
                    var newLearningTopic: LearningTopic = LearningTopic(idTopic!!, idChecked, idLearning, idLearned)

                    learningTopics.add(newLearningTopic)
                }


                learningTopics.add(learningTopic)
                db.child(usId).child("learningTopics").setValue(learningTopics)
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }

    fun getUserIdShareRef(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("my_shared_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("idUser", "undefined")!!
        return userId
    }


    fun getLearnedInfoByUser(userId: String, topicId: String, myF: (Int)-> Unit) {
        Log.e("topiccx", "{idLearned.size}")
        db.child(userId).get().addOnSuccessListener {
            var items = it.child("learningTopics")
            var learningTopics  = ArrayList(emptyList<LearningTopic>())
            if(items!=null) {
                for( item in items.children) {
                    var idTopic = item.child("idTopic").getValue(String::class.java)
                    if(idTopic == topicId) {
                        var idLearned1 = item.child("idLearned").getValue()
                        var idLearned:ArrayList<String>? = idLearned1 as? ArrayList<String>

                        if(idLearned==null) {
                            idLearned = ArrayList(emptyList<String>())
                        }
                        Log.e("topiccx", "${idLearned.size}")
                        myF(idLearned.size)
                    }
                }
            }
        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
    }


}
