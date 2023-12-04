package com.example.freshcard.Structure

import android.content.Context
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

public class Database() {
    private val databaseLocation : String = "https://freshcard-f4915-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val instant = FirebaseDatabase.getInstance(databaseLocation)

    fun getReference(url: String) : DatabaseReference{
        return instant.getReference(url)
    }
}