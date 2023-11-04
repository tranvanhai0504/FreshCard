package com.example.freshcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.renderscript.Sampler.Value
import android.util.Log
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.io.Console

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myRef = FirebaseDatabase.getInstance("https://freshcard-f4915-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("message")

        myRef.setValue("Hello, World!")
    }
}