package com.example.freshcard.Structure

import java.io.Serializable

data class Folder(val id: String, var name: String, val idTopics: ArrayList<String>, val idUser: String): Serializable{}
