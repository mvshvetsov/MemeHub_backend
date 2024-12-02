package ru.shvetsov.todoList.models.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object VideosTable : Table() {
    val id: Column<Int> = integer("id").autoIncrement()
    val user_id: Column<Int> = reference("user_id", UsersTable.id)
    val description: Column<String> = varchar("description", 100)
    val tag: Column<String> = varchar("tag", 20)
    val videoUrl: Column<String> = varchar("video_url", 100)

    override val primaryKey = PrimaryKey(id)
}