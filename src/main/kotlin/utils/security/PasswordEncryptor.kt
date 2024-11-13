package ru.shvetsov.todoList.utils.security

import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

class PasswordEncryptor(
    algorithm: String,
    private val transformation: String,
    private val keySize: Int,
    private val gcmTagLength: Int,
    private val ivSize: Int,
    secretKeyEnv: String
) {

    private var secretKey: SecretKey = SecretKeySpec(secretKeyEnv.toByteArray(), algorithm)

    fun encryptPassword(password: String): String {
        val cipher = Cipher.getInstance(transformation)
        val iv = ByteArray(ivSize)
        SecureRandom().nextBytes(iv)
        val ivSpec = GCMParameterSpec(gcmTagLength * 8, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(password.toByteArray())

        val ivAndEncrypted = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(ivAndEncrypted)
    }

    fun decryptPassword(encryptedPassword: String): String {
        val ivAndEncrypted = Base64.getDecoder().decode(encryptedPassword)

        val iv = ivAndEncrypted.sliceArray(0 until ivSize)
        val encryptedBytes = ivAndEncrypted.sliceArray(ivSize until ivAndEncrypted.size)

        val cipher = Cipher.getInstance(transformation)
        val ivSpec = GCMParameterSpec(gcmTagLength * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}