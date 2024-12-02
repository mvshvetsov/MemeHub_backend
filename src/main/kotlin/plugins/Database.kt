package ru.shvetsov.todoList.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.shvetsov.todoList.models.tables.UsersTable
import ru.shvetsov.todoList.models.tables.VideosTable

object DatabaseFactory {

    fun Application.initializeDatabase() {

        val dbUrl = environment.config.property("ktor.db.url").getString()
        val dbUser = environment.config.property("ktor.db.user").getString()
        val dbPassword = environment.config.property("ktor.db.password").getString()

        val config = HikariConfig().apply {
            jdbcUrl = dbUrl
            driverClassName = "org.postgresql.Driver"
            username = dbUser
            password = dbPassword
            maximumPoolSize = 6
            isReadOnly = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }

        val dataSource = HikariDataSource(config)
        Database.connect(datasource = dataSource)

        transaction {
            SchemaUtils.create(UsersTable, VideosTable)
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            transaction { block() }
        }
    }
}