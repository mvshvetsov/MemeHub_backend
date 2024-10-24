package ru.shvetsov.todoList.utils.security

import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class PasswordEncryptor(
    private val saltLength: Int,
    private val hashIterators: Int,
    private val keyLength: Int,
    private val algorithm: String
) {

    fun generateSalt(): String {
        val salt = ByteArray(saltLength)
        SecureRandom().nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }

    fun hashPassword(password: String, salt: String): String {
        val spec = PBEKeySpec(
            password.toCharArray(),
            Base64.getDecoder().decode(salt),
            hashIterators, keyLength
        )
        val factory = SecretKeyFactory.getInstance(algorithm)
        val hash = factory.generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(hash)
    }

    fun verifyPassword(password: String, salt: String, hashedPassword: String): Boolean {
        val generatedHash = hashPassword(password, salt)
        return generatedHash == hashedPassword
    }
}
