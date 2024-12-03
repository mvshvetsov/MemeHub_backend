package ru.shvetsov.todoList.responses

import kotlinx.serialization.Serializable

@Serializable
data class ProfileVideoResponse(
    val userId: Int,
    val description: String,
    val tag: String,
    val videoUrl: String,
    val thumbnailUrl: String
)
