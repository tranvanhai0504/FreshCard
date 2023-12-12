package com.example.freshcard.DAO

import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.ResultTest
import com.google.firebase.database.DatabaseReference

public class ResultTestDAO {
    var topicResult: DatabaseReference = Database().getReference("resultTopic")

    fun resultSave(idUser: String, amountCorrect: Int, duration: Int, time: String, type: String): Boolean{
        val key = topicResult.push().key

        if (key != null){
            var newResult = ResultTest(idUser, amountCorrect, duration,
                time, type)
            topicResult.child(key).setValue(newResult)
            return true
        }else{
            return false
        }
    }

}