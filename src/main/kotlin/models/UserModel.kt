package ru.shvetsov.todoList.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel (
    val id: Int,
    val login: String,
    val password: String,
    val salt: String
)