package kz.kff.core.config
import io.ktor.server.application.ApplicationEnvironment
import io.ktor.server.config.ApplicationConfig

data class SwaggerConfig(
    val title: String,
    val description: String? = "My Application Description",
    val version: String? = "1.0.0",
    val enabled: Boolean = false,
    val path: String = "/swagger",
) {
    companion object{
        fun configure(environment: ApplicationConfig): SwaggerConfig {
            val swagerConfig = environment.config("swagger")
            return SwaggerConfig(
                title =swagerConfig.property("title").getString(),
                description = swagerConfig.property("description").getString(),
                version = swagerConfig.property("version").getString(),
                enabled = swagerConfig.property("enabled").getString().toBoolean(),
                path = swagerConfig.property("path").getString(),
            )
        }
    }
}