package com.example.freshcard.DAO

import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.ResultTest
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
}