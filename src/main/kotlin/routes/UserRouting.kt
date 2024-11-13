package ru.shvetsov.todoList.routes

import com.auth0.jwt.JWT
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.shvetsov.todoList.responses.UserResponse
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.utils.constants.Constants.BASE_PORT
import ru.shvetsov.todoList.utils.constants.Constants.BASE_URL
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

fun Route.userRouting(
    userService: UserService,
    passwordEncryptor: PasswordEncryptor
) {

    authenticate("jwt") {
//        post("/update-user") {
//            val userRequest = call.receiveNullable<UserRequest>() ?: kotlin.run {
//                call.respond(HttpStatusCode.BadRequest)
//                return@post
//            }
//
//            try {
//                if (checkUserExist(userService, userRequest.id!!)) {
//                    val user = UserModel(
//                        id = userRequest.id,
//                        login = userRequest.login,
//                        username = userRequest.username,
//                        password = userRequest.password,
//                        salt = userRequest.salt
//                    )
//
//                    userService.updateUser(user = user)
//                    call.respond(HttpStatusCode.OK)
//                }
//            } catch (e: Exception) {
//                call.respond(HttpStatusCode.BadRequest)
//            }
//        }

        delete("/delete-user") {
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

        post("/user/profile-picture") {
            val multipart = call.receiveMultipart()
            var fileName: String? = null

            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        if (part.name == "image") {
                            fileName = "user_${System.currentTimeMillis()}.jpg"
                            part.save("src/photo", fileName!!)
                        }
                    }
                    else -> Unit
                }
                part.dispose()
            }

            if (fileName != null) {
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                val userId = JWT.decode(token).getClaim("id").asInt()
                val imagePath = fileName
                val updatedUser = userService.uploadProfilePicture(userId, imagePath.toString())

                if (updatedUser != null) {
                    call.respond(HttpStatusCode.OK, updatedUser)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to update user profile picture")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "No file uploaded")
            }
        }

        get("/user/profile") {
            val userId = call.request.queryParameters["id"]?.toInt()
            val user = userService.getUserById(userId!!)

            val userResponse = UserResponse(
                login = user!!.login,
                password = passwordEncryptor.encryptPassword(user.password),
                username = user.username,
                profilePicture = "http://${BASE_URL}:${BASE_PORT}/profile-pictures/${user.profilePicture}"
            )
            call.respond(HttpStatusCode.OK, userResponse)
        }
    }
}

private suspend fun checkUserExist(userService: UserService, id: Int): Boolean {
    return userService.getUserById(id = id) != null
}