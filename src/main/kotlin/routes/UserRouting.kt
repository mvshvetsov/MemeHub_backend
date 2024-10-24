package ru.shvetsov.todoList.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.models.UserModel
import ru.shvetsov.todoList.requests.UserRequest
import ru.shvetsov.todoList.services.UserService

fun Route.userRouting(userService: UserService) {

    authenticate("jwt") {
        post("/update-user") {
            val userRequest = call.receiveNullable<UserRequest>() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            try {
                if (checkUserExist(userService, userRequest.id!!)) {
                    val user = UserModel(
                        id = userRequest.id,
                        login = userRequest.login,
                        password = userRequest.password,
                        salt = userRequest.salt
                    )

                    userService.updateUser(user = user)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest)
            }
        }

        delete("delete-user") {
            val userRequest = call.request.queryParameters["id"]?.toInt() ?: kotlin.run {
                call.respond(HttpStatusCode.BadRequest)
                return@delete
            }

            try {
                if (checkUserExist(userService, userRequest)) {
                    userService.deleteUser(id = userRequest)
                    call.respond(HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict)
            }
        }
    }
}

private suspend fun checkUserExist(userService: UserService, id: Int): Boolean {
    return userService.getUserById(id = id) != null
}