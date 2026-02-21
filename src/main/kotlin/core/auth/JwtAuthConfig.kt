package kz.kff.core.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.jwt
import kz.kff.core.config.JWTConfig
import kz.kff.core.exception_handlers.api.ApiUnauthorizedException

const val JWT_ACCESS_AUTH = "jwt-access"

fun Application.configureJwtAuth(jwtConfig: JWTConfig) {
    authentication {
        jwt(JWT_ACCESS_AUTH) {
            realm = "soccer-referee-app"
            verifier(
                JWT.require(Algorithm.HMAC512(jwtConfig.secret))
                    .withIssuer(jwtConfig.issuer)
                    .withAudience(jwtConfig.audience)
                    .build()
            )
            validate { credential ->
                val type = credential.payload.getClaim("type").asString()
                if (type != "ACCESS") return@validate null

                val userId = credential.payload.subject?.toLongOrNull()
                    ?: return@validate null
                val sessionId = credential.payload.getClaim("sid").asLong()
                    ?: return@validate null
                val role = credential.payload.getClaim("role").asString()
                val permissions = credential.payload
                    .getClaim("permissions")
                    .asList(String::class.java)
                    ?: emptyList()

                AppPrincipal(
                    userId = userId,
                    sessionId = sessionId,
                    role = role,
                    permissions = permissions,
                )
            }
            challenge { _, _ ->
                throw ApiUnauthorizedException()
            }
        }
    }
}
