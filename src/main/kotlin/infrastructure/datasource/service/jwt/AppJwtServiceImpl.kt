package kz.kff.infrastructure.datasource.service.jwt

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import kz.kff.core.config.JWTConfig
import kz.kff.core.exception_handlers.api.ApiUnauthorizedException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.domain.datasource.service.jwt.ParsedRefreshToken
import kz.kff.domain.dto.user.UserWithRelationsDTO
import org.mindrot.jbcrypt.BCrypt
import java.util.Date

class AppJwtServiceImpl(
    private val jwtConfig: JWTConfig
) : AppJwtService {

    private val key = Keys.hmacShaKeyFor(jwtConfig.secret.toByteArray())

    override fun generateAccessToken(user: UserWithRelationsDTO, sessionId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(user.id.toString())
            .claim("email", user.email)
            .claim("role", user.role?.value)
            .claim("permissions", user.role?.permissions?.map { it.value } ?: emptyList<String>())
            .claim("sid", sessionId)
            .claim("type", "ACCESS")
            .issuer(jwtConfig.issuer)
            .audience().add(jwtConfig.audience).and()
            .issuedAt(now)
            .expiration(Date(now.time + jwtConfig.accessExpiration))
            .signWith(key)
            .compact()
    }

    override fun generateRefreshToken(userId: Long, sessionId: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("sid", sessionId)
            .claim("type", "REFRESH")
            .issuer(jwtConfig.issuer)
            .audience().add(jwtConfig.audience).and()
            .issuedAt(now)
            .expiration(Date(now.time + jwtConfig.refreshExpiration))
            .signWith(key)
            .compact()
    }

    override fun parseRefreshToken(token: String): ParsedRefreshToken {
        try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            val type = claims.get("type", String::class.java)
            if (type != "REFRESH") throw ApiUnauthorizedException()

            val userId = claims.subject?.toLongOrNull()
                ?: throw ApiUnauthorizedException()
            val sessionId = (claims["sid"] as? Number)?.toLong()
                ?: throw ApiUnauthorizedException()

            return ParsedRefreshToken(userId = userId, sessionId = sessionId)
        } catch (e: JwtException) {
            throw ApiUnauthorizedException()
        }
    }

    override fun refreshExpiresAt(): Long {
        return  System.currentTimeMillis() + jwtConfig.refreshExpiration
    }

    override fun refreshAccessExpiresAt(): Long {
        return  System.currentTimeMillis() + jwtConfig.accessExpiration
    }

    override fun hashPassword(password: String): String {
        val salt = BCrypt.gensalt(12)
        return BCrypt.hashpw(password, salt)
    }

    override fun verifyPassword(rawPassword: String, hashedPassword: String): Boolean {
        return BCrypt.checkpw(rawPassword, hashedPassword)
    }
}
