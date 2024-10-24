package ru.shvetsov.todoList.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.routes.authRouting
import ru.shvetsov.todoList.routes.userRouting
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.utils.jwt.JwtService
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

fun Application.configureRouting(userService: UserService, jwtService: JwtService, passwordEncryptor: PasswordEncryptor) {

    routing {
        authRouting(userService, jwtService, passwordEncryptor)
        userRouting(userService)
    }
}