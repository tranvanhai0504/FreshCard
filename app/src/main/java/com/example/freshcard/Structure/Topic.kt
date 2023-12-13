package com.example.freshcard.Structure

import java.io.Serializable

data class Topic(val id: String = "", val owner : String = "", var title : String = "", var items: ArrayList<TopicItem>? = null, var isPublic: Boolean = false, var learnedPeople : ArrayList<String>? = ArrayList<String>(), var timeCreated : Long = 0, var timePublish : Long = 0): Serializable{}
