package ru.shvetsov.todoList.routes

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.models.UserModel
import ru.shvetsov.todoList.requests.LoginRequest
import ru.shvetsov.todoList.requests.UserRequest
import ru.shvetsov.todoList.responses.BaseResponse
import ru.shvetsov.todoList.responses.LoginResponse
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
            val defaultProfilePicture = "default_photo.png"
            val user = UserModel(
                id = 0,
                login = userRequest.login,
                username = userRequest.username,
                password = userRequest.password,
                profilePicture = defaultProfilePicture
            )
            val existingUserByLogin = userService.getUserByLogin(userRequest.login)
            if (existingUserByLogin != null) {
                call.respond(HttpStatusCode.Conflict, BaseResponse(false, "User with login already exist"))
                return@post
            }
            val existingUserByUsername = userService.getUserByUsername(userRequest.username)
            if (existingUserByUsername != null) {
                call.respond(HttpStatusCode.Conflict, BaseResponse(false, "Username is already taken"))
                return@post
            }
            if (user.login.isNotBlank() && user.password.isNotBlank()) {
                userService.addUser(user = user)
                call.respond(HttpStatusCode.OK, BaseResponse(true, "Successful"))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, "Something went wrong"))
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
                return@post
            }
            if (passwordEncryptor.decryptPassword(user.password, passwordEncryptor.secretKeySpec) != loginRequest.password) {
                call.respond(HttpStatusCode.Conflict, BaseResponse(false, "Incorrect password"))
                return@post
            } else {
                call.respond(HttpStatusCode.OK, LoginResponse(true, user.id, jwtService.generateToken(user)))
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, BaseResponse(false, "Xuy"))
        }
    }
}