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
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (videoRequest == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing videoRequest")
                return@post
            }

            if (videoUrl == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing video")
                return@post
            }

            val video = VideoModel(
                id = 0,
                userId = videoRequest!!.userId,
                description = videoRequest!!.description,
                tag = videoRequest!!.tag,
                videoUrl = videoUrl!!
            )

            videoService.addVideo(video)
            call.respond(HttpStatusCode.OK, BaseResponse(true, "Video upload successfully"))
        }
    }
}