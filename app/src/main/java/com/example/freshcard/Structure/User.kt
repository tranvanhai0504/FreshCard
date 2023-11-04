package com.example.freshcard.Structure

import com.google.type.DateTime

data class User(val username: String, var password : String, var fullName : String, var avatar : String, var email : String, var phoneNumber : String, var bookmarked_topic : ArrayList<String>, var lastAccess : DateTime, var learingTopics : ArrayList<LearningTopic>){}
