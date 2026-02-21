package kz.kff.core.config

import io.ktor.server.config.ApplicationConfig

class JWTConfig(
    val secret:String,
    val issuer:String,
    val audience:String,
    val accessExpiration:Long,
    val refreshExpiration:Long,
) {
    companion object{
        fun configure(config: ApplicationConfig): JWTConfig {
            val jwtConfig = config.config("jwt")
            return JWTConfig(
                secret = jwtConfig.property("secret").getString(),
                issuer = jwtConfig.property("issuer").getString(),
                audience = jwtConfig.property("audience").getString(),
                accessExpiration = jwtConfig.property("access_expiration").getString().toLong(),
                refreshExpiration = jwtConfig.property("refresh_expiration").getString().toLong(),
            )
        }
    }
}