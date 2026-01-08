package com.hsryuuu.careerbuilder.application.security

import com.hsryuuu.careerbuilder.application.property.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey: String
        get() = jwtProperties.secret

    val validityInMilliseconds: Long
        get() = jwtProperties.accessTokenExp

    fun createToken(userInfo: UserInfo): String {
        val claims: Claims = Jwts.claims().setSubject(userInfo.username)
        val now = Date()
        val validity = Date(now.time + validityInMilliseconds)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .addClaims(
                mapOf(
                    "id" to userInfo.id,
                    "username" to userInfo.username,
                    "email" to userInfo.email,
                    "role" to userInfo.role.name
                )
            )
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String, userInfo: UserInfo): Boolean {
        val user = getUserFromToken(token)
        return (user.username == userInfo.username && !isTokenExpired(token))
    }

    fun getUserFromToken(token: String): UserInfo {
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

        return UserInfo(
            id = UUID.fromString(claims["id"] as String),
            username = claims["username"] as String,
            email = claims["email"] as String,
            role = UserRole.valueOf(claims["role"] as String)
        )
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body
            .expiration
        return expiration.before(Date())
    }

}
