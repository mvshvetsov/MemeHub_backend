package ru.shvetsov.todoList.routes

import io.ktor.http.content.*
import io.ktor.utils.io.jvm.javaio.*
import java.io.File

fun PartData.FileItem.save(path: String, fileName: String): String {
    val inputStream = provider().toInputStream()
    val fileBytes = inputStream.readBytes()
    val folder = File(path)
    folder.mkdirs()
    val fullPath = File("$path/$fileName")
    fullPath.writeBytes(fileBytes)
    return fileName
}
