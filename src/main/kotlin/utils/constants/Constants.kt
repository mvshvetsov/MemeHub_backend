package ru.shvetsov.todoList.utils.constants

import io.ktor.server.engine.*

object Constants {
    private val environment = CommandLineConfig(arrayOf())
    val BASE_URL = environment.environment.config.property("ktor.deployment.host").getString()
    val BASE_PORT = environment.environment.config.property("ktor.deployment.port").getString().toInt()
}