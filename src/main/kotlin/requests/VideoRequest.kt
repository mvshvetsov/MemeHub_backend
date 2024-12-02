package ru.shvetsov.todoList.requests

import kotlinx.serialization.Serializable

@Serializable
data class VideoRequest(
    val userId: Int,
    val description: String,
    val tag: String
)