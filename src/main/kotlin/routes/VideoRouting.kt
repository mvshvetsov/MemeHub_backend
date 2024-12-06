package ru.shvetsov.todoList.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import ru.shvetsov.todoList.models.VideoModel
import ru.shvetsov.todoList.requests.VideoRequest
import ru.shvetsov.todoList.responses.BaseResponse
import ru.shvetsov.todoList.services.VideoService

fun Route.videoRouting(
    videoService: VideoService
) {

    authenticate("jwt") {

        post("upload/video") {
            val multipart = call.receiveMultipart()
            var videoUrl: String? = null
            var thumbnailUrl: String? = null
            var videoRequest: VideoRequest? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "videoRequest") {
                            val requestData = part.value
                            println("Received videoRequest: $requestData")
                            videoRequest = Json.decodeFromString(part.value)
                        }
                    }
                    is PartData.FileItem -> {
                        if (part.name == "video") {
                            val fileName = "video_${System.currentTimeMillis()}.mp4"
                            videoUrl = part.save("src/video", fileName)
                        }
                        if (part.name == "thumbnail") {
                            val fileName = "thumbnail_${System.currentTimeMillis()}.jpg"
                            thumbnailUrl = part.save("src/thumbnail", fileName)
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (videoRequest == null || videoUrl == null || thumbnailUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing data")
                return@post
            }

            val video = VideoModel(
                id = 0,
                userId = videoRequest!!.userId,
                description = videoRequest!!.description,
                tag = videoRequest!!.tag,
                videoUrl = videoUrl!!,
                thumbnailUrl = thumbnailUrl!!
            )

            videoService.addVideo(video)
            call.respond(HttpStatusCode.OK, BaseResponse(true, "Video upload successfully"))
        }

        get("/user/videos") {
            val userId = call.parameters["user_id"]?.toInt()
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, BaseResponse(false, "Invalid user ID"))
                return@get
            }
            try {
                val profileVideos = videoService.getVideosByUserId(userId)
                call.respond(HttpStatusCode.OK, profileVideos)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, BaseResponse(false, "Failed to retrieve videos"))
            }
        }

        get("/videos") {
            val randomVideos = videoService.getRandomVideos()
            call.respond(HttpStatusCode.OK, randomVideos)
        }
    }
}