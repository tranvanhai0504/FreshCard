package com.example.freshcard.Structure

import com.google.type.DateTime
import java.time.LocalDateTime

data class User(var password: String? = "", var fullName: String? = "", var avatar: String? = "", var email: String? = "", var phoneNumber: String? = "", var bookmarkedTopic: ArrayList<String>? = ArrayList<String>(), var lastAccess: Long? = 0, var learningTopics: ArrayList<LearningTopic>? = ArrayList()){}
