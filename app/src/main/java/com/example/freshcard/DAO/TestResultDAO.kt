package com.example.freshcard.DAO

import android.util.Log
import com.example.freshcard.MainActivity
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.Folder
import com.example.freshcard.Structure.ResultTest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import org.bouncycastle.util.test.Test
import org.bouncycastle.util.test.TestResult

class TestResultDAO {

    private var testResultRef = Database().getReference("testResult")

    fun pushTestResult(testResult: ResultTest) {
        testResultRef.get().addOnSuccessListener {
            var ls = ArrayList<ResultTest>()
            for(item in it.children) {
                var userId = item.child("idUser").getValue(String::class.java)
                var amountCorrect = item.child("amountCorrect").getValue(Int::class.java)
                var duration = item.child("duration").getValue(Int::class.java)
                var time = item.child("time").getValue(String::class.java)
                var type = item.child("type").getValue(String::class.java)
                var topicId = item.child("idTopic").getValue(String::class.java)
                ls.add(ResultTest(userId!!,topicId!!,amountCorrect!!,duration!!,time!!,type!!))
            }
            ls.add(testResult)
            testResultRef.setValue(ls)
        }
    }

    fun getTestResult(myF: (HashMap<String, ArrayList<ResultTest>>)->Unit) {
        testResultRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var hashmap = HashMap<String, ArrayList<ResultTest>>()
                for (snapshot in dataSnapshot.children) {
                    val amountCorrect = snapshot.child("amountCorrect").getValue(Int::class.java)
                    val duration = snapshot.child("duration").getValue(Int::class.java)
                    val idTopic = snapshot.child("idTopic").getValue(String::class.java)
                    var idUser = snapshot.child("idUser").getValue(String::class.java)
                    var time = snapshot.child("time").getValue(String::class.java)
                    var type = snapshot.child("type").getValue(String::class.java)
                    if(hashmap.containsKey(idTopic)) {
                        hashmap[idTopic]!!.add(ResultTest(idUser!!,idTopic!!,amountCorrect!!,duration!!,time!!, type!!))
                    }else {
                        var arr = ArrayList<ResultTest>()
                        arr.add(ResultTest(idUser!!,idTopic!!,amountCorrect!!,duration!!,time!!, type!!))
                        hashmap[idTopic] = arr
                    }
                }

                for(item in hashmap) {
                    item.value.sortBy { it.duration }
                    item.value.sortByDescending { it.amountCorrect }
                }
                myF(hashmap)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

    fun getRankingByTopic(topicId: String, myF: (ArrayList<ResultTest>)-> Unit) {
        testResultRef.orderByChild("idTopic").equalTo(topicId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var array = ArrayList<ResultTest>()
                for (snapshot in dataSnapshot.children) {
                    val amountCorrect = snapshot.child("amountCorrect").getValue(Int::class.java)
                    val duration = snapshot.child("duration").getValue(Int::class.java)
                    val idTopic = snapshot.child("idTopic").getValue(String::class.java)
                    var idUser = snapshot.child("idUser").getValue(String::class.java)
                    var time = snapshot.child("time").getValue(String::class.java)
                    var type = snapshot.child("type").getValue(String::class.java)
                    array.add(ResultTest(idUser!!,idTopic!!,amountCorrect!!,duration!!,time!!, type!!))
                }

                array.sortBy { it.duration }
                array.sortByDescending { it.amountCorrect }
                myF(array)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }
}