package ru.shvetsov.todoList.services

import org.jetbrains.exposed.sql.*
import ru.shvetsov.todoList.models.VideoModel
import ru.shvetsov.todoList.models.tables.UsersTable
import ru.shvetsov.todoList.models.VideoWithUserInfoModel
import ru.shvetsov.todoList.models.tables.VideosTable
import ru.shvetsov.todoList.plugins.DatabaseFactory.dbQuery
import ru.shvetsov.todoList.utils.constants.Constants.BASE_PORT
import ru.shvetsov.todoList.utils.constants.Constants.BASE_URL

class VideoService {

    suspend fun addVideo(video: VideoModel) {
        dbQuery {
            VideosTable.insert { table ->
                table[user_id] = video.userId
                table[description] = video.description
                table[tag] = video.tag
                table[videoUrl] = video.videoUrl
                table[thumbnailUrl] = video.thumbnailUrl
            }
        }
    }

    suspend fun getVideosByUserId(userId: Int): List<VideoWithUserInfoModel> {
        return dbQuery {
            (VideosTable innerJoin UsersTable)
                .select(
                    VideosTable.description,
                    VideosTable.tag,
                    VideosTable.videoUrl,
                    VideosTable.thumbnailUrl,
                    UsersTable.username,
                    UsersTable.profilePicture
                )
                .where { UsersTable.id eq VideosTable.user_id and(VideosTable.user_id eq userId) }
                .mapNotNull { rowToVideoWithUserModel(it) }
        }
    }

    suspend fun getRandomVideos(): List<VideoWithUserInfoModel> {
        return dbQuery {
            (VideosTable innerJoin UsersTable)
                .select(
                    VideosTable.description,
                    VideosTable.tag,
                    VideosTable.videoUrl,
                    VideosTable.thumbnailUrl,
                    UsersTable.username,
                    UsersTable.profilePicture
                )
                .where { UsersTable.id eq VideosTable.user_id }
                .orderBy(Random())
                .limit(10)
                .mapNotNull { rowToVideoWithUserModel(it) }
        }
    }

    private fun rowToVideoWithUserModel(row: ResultRow?): VideoWithUserInfoModel? {
        if (row == null) {
            return null
        }

        val videoUrl = row[VideosTable.videoUrl]
        val thumbnailUrl = row[VideosTable.thumbnailUrl]
        val profilePicture = row[UsersTable.profilePicture]

        val fullVideoUrl = "http://$BASE_URL:$BASE_PORT/user/videos/$videoUrl"
        val fullThumbnailUrl = "http://$BASE_URL:$BASE_PORT/thumbnail/$thumbnailUrl"
        val fullProfilePictureUrl = "http://$BASE_URL:$BASE_PORT/profile-pictures/$profilePicture"

        return VideoWithUserInfoModel(
            description = row[VideosTable.description],
            tag = row[VideosTable.tag],
            videoUrl = fullVideoUrl,
            thumbnailUrl = fullThumbnailUrl,
            username = row[UsersTable.username],
            profilePicture = fullProfilePictureUrl
        )
    }

    //    private fun rowToVideoModel(row: ResultRow?): VideoModel? {
//        if (row == null) {
//            return null
//        }
//        val videoUrl = row[VideosTable.videoUrl]
//        val thumbnailUrl = row[VideosTable.thumbnailUrl]
//
//        val fullVideoUrl = "http://$BASE_URL:$BASE_PORT/user/videos/$videoUrl"
//        val fullThumbnailUrl = "http://$BASE_URL:$BASE_PORT/thumbnail/$thumbnailUrl"
//
//        return VideoModel(
//            id = row[VideosTable.id],
//            userId = row[VideosTable.user_id],
//            description = row[VideosTable.description],
//            tag = row[VideosTable.tag],
//            videoUrl = fullVideoUrl,
//            thumbnailUrl = fullThumbnailUrl
//        )
//    }
}