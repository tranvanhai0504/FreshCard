package com.example.freshcard.Structure

data class Topic(val id: String, val owner : String, var title : String, var items: ArrayList<TopicItem>, var isPublic: Boolean, var learnedPeople : ArrayList<String>){}
