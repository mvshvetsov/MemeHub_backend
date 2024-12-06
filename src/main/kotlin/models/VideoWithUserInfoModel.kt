package ru.shvetsov.todoList.models

import kotlinx.serialization.Serializable

@Serializable
data class VideoWithUserInfoModel(
    val description: String,
    val tag: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val username: String,
    val profilePicture: String
)
