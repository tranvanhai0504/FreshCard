package com.example.freshcard.Structure

data class TopicInfoView(val topicId: String, val topicName: String, val totalCards: Int, val totalLearned: Int, val timeAccess: String, val access: Boolean, val owner:String) {
}