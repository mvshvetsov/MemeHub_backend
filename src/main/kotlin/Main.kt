package ru.shvetsov.todoList

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import ru.shvetsov.todoList.plugins.DatabaseFactory.initializeDatabase
import ru.shvetsov.todoList.plugins.configureRouting
import ru.shvetsov.todoList.plugins.configureSecurity
import ru.shvetsov.todoList.plugins.configureSerialization
import ru.shvetsov.todoList.services.UserService
import ru.shvetsov.todoList.services.VideoService
import ru.shvetsov.todoList.utils.constants.Constants.BASE_PORT
import ru.shvetsov.todoList.utils.constants.Constants.BASE_URL
import ru.shvetsov.todoList.utils.jwt.JwtService
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

fun main() {
    embeddedServer(Netty,
        environment = applicationEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        },
        configure = {
            connector {
                port = BASE_PORT
                host = BASE_URL
            }
        },
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val algorithm = environment.config.property("ktor.encryption.algorithm").getString()
    val transformation = environment.config.property("ktor.encryption.transformation").getString()
    val secretKey = environment.config.property("ktor.encryption.secretKey").getString()

    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()

    val jwtService = JwtService(secret, issuer)
    val passwordEncryptor = PasswordEncryptor(secretKey, algorithm, transformation)
    val userService = UserService(passwordEncryptor)
    val videoService = VideoService()

    configureSecurity(userService, jwtService)
    configureRouting(userService = userService, jwtService = jwtService, passwordEncryptor = passwordEncryptor, videoService = videoService)
    initializeDatabase()
    configureSerialization()
}