package com.example.freshcard.Structure

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object database {
    val databaseLocation : String = "https://freshcard-f4915-default-rtdb.asia-southeast1.firebasedatabase.app"
    val instant = FirebaseDatabase.getInstance(databaseLocation)

    fun getReference(url: String) : DatabaseReference{
        return instant.getReference(url)
    }
}