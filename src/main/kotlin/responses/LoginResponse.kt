package ru.shvetsov.todoList.responses

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val isSuccess: Boolean,
    val id: Int,
    val token: String
)
