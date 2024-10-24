package ru.shvetsov.todoList.utils.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import ru.shvetsov.todoList.models.UserModel
import java.time.LocalDateTime
import java.time.ZoneOffset

class JwtService(private val secret: String, private val issuer: String) {

    fun generateToken(user: UserModel): String {
        return JWT.create()
            .withIssuer(issuer)
            .withClaim("login", user.login)
            .withExpiresAt(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC))
            .sign(Algorithm.HMAC256(secret))
    }

    fun getVerifier(): JWTVerifier {
        return JWT
            .require(Algorithm.HMAC256(secret))
            .withIssuer(issuer)
            .build()
    }
}
