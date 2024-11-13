package ru.shvetsov.todoList.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse (
    val login: String,
    val password: String,
    val username: String,
    val profilePicture: String
)