package ru.shvetsov.todoList.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.shvetsov.todoList.models.UserModel
import ru.shvetsov.todoList.models.tables.UsersTable
import ru.shvetsov.todoList.plugins.DatabaseFactory.dbQuery
import ru.shvetsov.todoList.requests.UpdateUserRequest
import ru.shvetsov.todoList.responses.UserResponse
import ru.shvetsov.todoList.utils.constants.Constants.BASE_PORT
import ru.shvetsov.todoList.utils.constants.Constants.BASE_URL
import ru.shvetsov.todoList.utils.security.PasswordEncryptor

class UserService(
    private val passwordEncryptor: PasswordEncryptor
) {
    suspend fun addUser(user: UserModel) {
        val hashPassword = passwordEncryptor.encryptPassword(user.password, passwordEncryptor.secretKeySpec)
        dbQuery {
            UsersTable.insert { table ->
                table[login] = user.login
                table[username] = user.username
                table[password] = hashPassword
                table[profilePicture] = user.profilePicture
            }
        }
    }

    suspend fun deleteUser(id: Int) {
        dbQuery {
            UsersTable.deleteWhere { UsersTable.id eq id }
        }
    }

    suspend fun updateUser(userId: Int, updateUserRequest: UpdateUserRequest) {
        dbQuery {
            UsersTable.update({ UsersTable.id eq userId }) { table ->
                updateUserRequest.username?.let { table[username] = it }
                updateUserRequest.login?.let { table[login] = it }
                updateUserRequest.password?.let { table[password] = passwordEncryptor.encryptPassword(it, passwordEncryptor.secretKeySpec) }
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

    suspend fun getUserByUsername(username: String): UserModel? {
        return dbQuery {
            UsersTable.selectAll().where { UsersTable.username eq username }
                .mapNotNull { rowToUserModel(it) }
                .singleOrNull()
        }
    }

    suspend fun uploadProfilePicture(id: Int, photo: String): UserResponse? {
        dbQuery {
            UsersTable.update({ UsersTable.id eq id }) { table ->
                table[profilePicture] = photo
            }
        }

        val user = getUserById(id)
        return user?.let {
            UserResponse(
                login = it.login,
                password = it.password,
                username = it.username,
                profilePicture = "http://$BASE_URL:$BASE_PORT/profile-pictures/$photo"
            )
        }
    }

    private fun rowToUserModel(row: ResultRow?): UserModel? {
        if (row == null) {
            return null
        }
        return UserModel(
            id = row[UsersTable.id],
            login = row[UsersTable.login],
            username = row[UsersTable.username],
            password = row[UsersTable.password],
            profilePicture = row[UsersTable.profilePicture].toString()
        )
    }
}