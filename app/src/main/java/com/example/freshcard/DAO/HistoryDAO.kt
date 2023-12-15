package com.example.freshcard.DAO

import android.util.Log
import com.example.freshcard.Structure.Database
import com.example.freshcard.Structure.History
import com.example.freshcard.Structure.TopicInfoView
import com.example.freshcard.Structure.TopicItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.type.DateTime
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class HistoryDAO {
    private val historyRef = Database().getReference("history")
    private val format = "yyyy-MM-dd'T'HH:mm:ss"


    fun pushHistory(userId: String, topicId: String, date: Date){
        var isSet = false
        historyRef.get().addOnSuccessListener { ref ->
            var historyList: ArrayList<History> = ArrayList()
            for(item in ref.children) {
                var usId = item.child("idUser").getValue(String::class.java)
                var tpId = item.child("idTopic").getValue(String::class.java)
                var d = item.child("date")
                var dates: ArrayList<Date> = ArrayList()
                for(i in d.children) {
                    var dateOjb = i.getValue() as Map<String, Any>
                    dates.add(getDate(dateOjb)!!)
                    Log.e("history", "...${d.children}")
                }
                historyList.add(History(idTopic = tpId!!, idUser = usId!!, date = dates))
                if(usId == userId && tpId == topicId) {
                    Log.e("history", "...${usId} == $userId")
                    dates.add(date)
                    isSet = true
                }
            }

            if(!isSet) {
                Log.e("history", "...didnt set")
                var array = ArrayList<Date>()
                array.add(date)
                historyList.add(History(idTopic = topicId!!, idUser = userId!!, date = array))
            }
            historyRef.setValue(historyList)




        }
    }

    fun getLastAccess(userId: String, topicId: String, myF: (String) -> Unit) {
        historyRef.orderByChild("idUser").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(item in dataSnapshot.children) {
                    var tpId = item.child("idTopic").getValue(String::class.java)
                    if(tpId == topicId) {
                        var d = item.child("date")
                        var dates: ArrayList<Date> = ArrayList()
                        for(i in d.children) {
                            var dateOjb = i.getValue() as Map<String, Any>
                            dates.add(getDate(dateOjb)!!)
                            Log.e("history", "...${d.children}")
                        }
                        myF(dates.get(dates.size-1).toString())
                        Log.e("result", "date ${dates}")
                    }
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                Log.e("result", "calcle")
            }
        })





    }



//    fun getDateList(usId: String, tpId: String, myF: (ArrayList<Date>)-> Unit) {
//        var dateList = ArrayList<Date>()
//        historyRef.get()
//            .addOnSuccessListener {
//                for(history in it.children) {
//                    var d = history.child("date").getValue()
//                    var userId = history.child("idUser").getValue(String::class.java)
//                    var topicId = history.child("idTopic").getValue(String::class.java)
//                    if(userId == usId && tpId == topicId) {
//                        Log.e("result", "--$d")
//                        if(d!=null) {
//                            var dates: ArrayList<String> = d as ArrayList<String>
//                            for(item in dates) {
//                                val dateTime = stringToDateTime(item, format)
//                                dateList.add(dateTime!!)
//                            }
//                        }
//                        myF(dateList)
//                    }
//
//                }
//            }
//
//    }

    fun stringToDateTime(dateString: String, format: String): Date? {
        return try {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun getDate(dateObject: Map<String, Any>): Date? {
        val year = dateObject["year"].toString().toInt()
        val month =
            dateObject["month"].toString().toInt() + 1 // Adjust month since it's zero-indexed
        val day = dateObject["date"].toString().toInt()
        val hour = dateObject["hours"].toString().toInt()
        val minute = dateObject["minutes"].toString().toInt()
        val second = dateObject["seconds"].toString().toInt()

        return Date.from(
            LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault())
                .toInstant()
            )
        }
}
