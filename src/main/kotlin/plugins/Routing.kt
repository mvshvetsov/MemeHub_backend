package ru.shvetsov.todoList.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.routes.authRouting
import ru.shvetsov.todoList.routes.userRouting
import ru.shvetsov.todoList.routes.videoRouting
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.services.VideoService
import ru.shvetsov.todoList.utils.jwt.JwtService
import ru.shvetsov.todoList.utils.security.PasswordEncryptor
import java.io.File

fun Application.configureRouting(userService: UserService, jwtService: JwtService, passwordEncryptor: PasswordEncryptor, videoService: VideoService) {

    routing {
        authRouting(userService, jwtService, passwordEncryptor)
        userRouting(userService, passwordEncryptor)
        staticFiles("/profile-pictures", File("src/photo"))
        staticFiles("/user/videos", File("src/video"))
        staticFiles("/thumbnail", File("src/thumbnail"))
        videoRouting(videoService)
    }
}