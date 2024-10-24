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
import ru.shvetsov.todoList.utils.jwt.JwtService
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

fun main() {
    embeddedServer(Netty,
        environment = applicationEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application.conf"))
        },
        configure = {
            connector {
                port = 8080
                host = "localhost"
            }
        },
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val saltLength = environment.config.property("ktor.encryption.saltLength").getString().toInt()
    val hashIterators = environment.config.property("ktor.encryption.hashIterators").getString().toInt()
    val keyLength = environment.config.property("ktor.encryption.keyLength").getString().toInt()
    val algorithm = environment.config.property("ktor.encryption.algorithm").getString()

    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()

    val jwtService = JwtService(secret, issuer)
    val passwordEncryptor = PasswordEncryptor(saltLength, hashIterators, keyLength, algorithm)
    val userService = UserService(passwordEncryptor)

    configureSecurity(userService, jwtService)
    configureRouting(userService = userService, jwtService = jwtService, passwordEncryptor = passwordEncryptor)
    initializeDatabase()
    configureSerialization()
}