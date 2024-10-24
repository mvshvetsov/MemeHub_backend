package ru.shvetsov.todoList.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.utils.jwt.JwtService

fun Application.configureSecurity(userService: UserService, jwtService: JwtService) {
    authentication {
        jwt("jwt") {
            verifier(jwtService.getVerifier())
            realm = "Service Server"
            validate {
                val payload = it.payload
                val login = payload.getClaim("email").asString()
                val user = userService.getUserByLogin(login = login)
                user
            }
        }
    }
}