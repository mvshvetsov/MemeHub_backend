package ru.shvetsov.todoList.models.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UsersTable: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val login: Column<String> = varchar("login", 100).uniqueIndex()
    val username: Column<String> = varchar("username", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 100)
    val profilePicture: Column<String?> = varchar("profile_picture", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}