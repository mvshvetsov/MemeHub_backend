package ru.shvetsov.todoList.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.models.UserModel
import ru.shvetsov.todoList.requests.LoginRequest
import ru.shvetsov.todoList.requests.UserRequest
import ru.shvetsov.todoList.responses.BaseResponse
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.utils.jwt.JwtService
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

fun Route.authRouting(
    userService: UserService,
    jwtService: JwtService,
    passwordEncryptor: PasswordEncryptor
) {
    post("/register") {
        val userRequest = call.receiveNullable<UserRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            val user = UserModel(
                id = 0,
                login = userRequest.login,
                password = userRequest.password,
                salt = ""
            )
            if (user.login.isNotBlank() && user.password.isNotBlank()) {
                userService.addUser(user = user)
                call.respond(HttpStatusCode.OK, BaseResponse(true, "Successful"))
            }

        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict)
        }
    }

    post("/login") {
        val loginRequest = call.receiveNullable<LoginRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@post
        }

        try {
            val user = userService.getUserByLogin(loginRequest.login)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound, BaseResponse(false, "User not found"))
            }
            if (passwordEncryptor.verifyPassword(loginRequest.password, user?.salt!!, user.password)) {
                call.respond(jwtService.generateToken(user))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict)
        }
    }
}