package ru.shvetsov.todoList.utils.security

import com.typesafe.config.ConfigFactory
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import java.security.SecureRandom



class PasswordEncryptor(private val secretKey: String) {

    private val secretKeyBase64 = secretKey
    private val decodedKey = Base64.getDecoder().decode(secretKeyBase64)
    val secretKeySpec = SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")

    // Функция для шифрования пароля
    fun encryptPassword(password: String, secretKey: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Генерация 12-байтового IV для GCM
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)
        val ivSpec = GCMParameterSpec(128, iv)

        // Инициализация шифра
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(password.toByteArray())

        // Конкатенация IV и зашифрованного текста
        val ivAndEncrypted = iv + encryptedBytes
        return Base64.getEncoder().encodeToString(ivAndEncrypted)
    }

    // Функция для дешифрования пароля
    fun decryptPassword(encryptedPassword: String, secretKey: SecretKey): String {
        val ivAndEncryptedBytes = Base64.getDecoder().decode(encryptedPassword)

        // Извлечение IV (первые 12 байт) и зашифрованного текста
        val iv = ivAndEncryptedBytes.copyOfRange(0, 12)
        val encryptedBytes = ivAndEncryptedBytes.copyOfRange(12, ivAndEncryptedBytes.size)

        // Настройка параметров GCM для дешифрования
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val ivSpec = GCMParameterSpec(128, iv)

        // Инициализация дешифратора
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }
}