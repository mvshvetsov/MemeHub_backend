package ru.shvetsov.todoList.utils.security

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import java.security.SecureRandom


class PasswordEncryptor(
    secretKey: String,
    algorithm: String,
    private val transformation: String
) {

    private val secretKeyBase64 = secretKey
    private val decodedKey = Base64.getDecoder().decode(secretKeyBase64)
    val secretKeySpec = SecretKeySpec(decodedKey, 0, decodedKey.size, algorithm)

    fun encryptPassword(password: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance(transformation)
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val ivSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(password.toByteArray())
        val ivAndEncrypted = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(ivAndEncrypted)
    }

    fun decryptPassword(encryptedPassword: String, secretKey: SecretKey): String {
        val ivAndEncryptedBytes = Base64.getDecoder().decode(encryptedPassword)
        val iv = ivAndEncryptedBytes.copyOfRange(0, 12)
        val encryptedBytes = ivAndEncryptedBytes.copyOfRange(12, ivAndEncryptedBytes.size)
        val cipher = Cipher.getInstance(transformation)
        val ivSpec = GCMParameterSpec(128, iv)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
}