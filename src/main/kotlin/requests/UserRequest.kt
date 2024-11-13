package ru.shvetsov.todoList.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val id: Int? = null,
    val login: String,
    val username: String,
    val password: String
)
