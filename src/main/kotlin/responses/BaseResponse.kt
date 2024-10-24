package ru.shvetsov.todoList.responses

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse (
    val success: Boolean,
    val message: String
)