package com.example.freshcard.Structure

import com.google.type.DateTime
import java.io.Serializable

data class ResultTest(val idUser: String, val idTopic: String, val amountCorrect: Int, val duration: Int, val time: String, val type: String): Serializable{}
