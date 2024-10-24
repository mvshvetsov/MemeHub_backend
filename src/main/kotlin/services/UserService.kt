package ru.shvetsov.todoList.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shvetsov.todoList.models.UserModel
import ru.shvetsov.todoList.models.tables.UsersTable
import ru.shvetsov.todoList.plugins.DatabaseFactory.dbQuery
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

class UserService(
    private val passwordEncryptor: PasswordEncryptor
) {
    suspend fun addUser(user: UserModel) {
        val generatedSalt = passwordEncryptor.generateSalt()
        val hashPassword = passwordEncryptor.hashPassword(user.password, generatedSalt)
        dbQuery {
            UsersTable.insert { table ->
                table[login] = user.login
                table[password] = hashPassword
                table[salt] = generatedSalt
            }
        }
    }

    suspend fun deleteUser(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id }
        }
    }

    suspend fun updateUser(user: UserModel) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq user.id }) { table ->
                table[login] = user.login
                table[password] = user.password
            }
        }
    }

    suspend fun getUserById(id: Int): UserModel? {
        return dbQuery {
            UsersTable.selectAll().where { UsersTable.id eq id }
                .mapNotNull { rowToUserModel(it) }
                .singleOrNull()
        }
    }

    suspend fun getUserByLogin(login: String): UserModel? {
        return dbQuery {
            UsersTable.selectAll().where { UsersTable.login eq login }
                .mapNotNull { rowToUserModel(it) }
                .singleOrNull()
        }
    }

    private fun rowToUserModel(row: ResultRow?): UserModel? {
        if (row == null) {
            return null
        }
        return UserModel(
            id = row[UsersTable.id],
            login = row[UsersTable.login],
            password = row[UsersTable.password],
            salt = row[UsersTable.salt]
        )
    }
}