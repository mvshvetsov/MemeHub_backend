package ru.shvetsov.todoList.services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import ru.shvetsov.todoList.models.VideoModel
import ru.shvetsov.todoList.models.tables.VideosTable
import ru.shvetsov.todoList.plugins.DatabaseFactory.dbQuery

class VideoService {

    suspend fun addVideo(video: VideoModel) {
        dbQuery {
            VideosTable.insert { table ->
                table[user_id] = video.userId
                table[description] = video.description
                table[tag] = video.tag
                table[videoUrl] = video.videoUrl
            }
        }
    }

    private fun rowToVideoModel(row: ResultRow?): VideoModel? {
        if (row == null) {
            return null
        }
        return VideoModel(
            id = row[VideosTable.id],
            userId = row[VideosTable.user_id],
            description = row[VideosTable.description],
            tag = row[VideosTable.tag],
            videoUrl = row[VideosTable.videoUrl]
        )
    }
}