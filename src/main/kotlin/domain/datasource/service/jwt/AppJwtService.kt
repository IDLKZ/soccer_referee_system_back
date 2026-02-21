package kz.kff.domain.datasource.service.jwt

import kz.kff.domain.dto.user.UserWithRelationsDTO

data class ParsedRefreshToken(val userId: Long, val sessionId: Long)

interface AppJwtService {

    fun generateAccessToken(
        user: UserWithRelationsDTO,
        sessionId: Long,
    ): String

    fun generateRefreshToken(
        userId: Long,
        sessionId: Long,
    ): String

    fun parseRefreshToken(token: String): ParsedRefreshToken

    fun refreshExpiresAt(): Long
    fun refreshAccessExpiresAt(): Long

    fun hashPassword(password: String): String

    fun verifyPassword(
        rawPassword: String,
        hashedPassword: String
    ): Boolean
}