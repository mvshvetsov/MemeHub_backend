package ru.shvetsov.todoList.requests

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val login: String? = null,
    val password: String? = null
)
