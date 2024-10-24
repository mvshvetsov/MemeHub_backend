package ru.shvetsov.todoList.models.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UsersTable: Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val login: Column<String> = varchar("login", 100).uniqueIndex()
    val password: Column<String> = varchar("password", 100)
    val salt: Column<String> = varchar("salt", 50)

    override val primaryKey = PrimaryKey(id)
}