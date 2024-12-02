package ru.shvetsov.todoList.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoModel(
    val id: Int,
    val userId: Int,
    val description: String,
    val tag: String,
    val videoUrl: String
)
