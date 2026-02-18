package kz.kff.core.config

import io.ktor.server.config.ApplicationConfig

data class AppEnvironmentConfig(
    val environment: AppEnvironment
) {
    companion object {
        fun configure(config: ApplicationConfig): AppEnvironmentConfig {
            val raw = System.getenv("KTOR_ENV")
                ?: config.propertyOrNull("ktor.deployment.environment")?.getString()
                ?: "dev"

            return AppEnvironmentConfig(
                environment = AppEnvironment.from(raw)
            )
        }
    }
}
enum class AppEnvironment {
    DEV, TEST, PROD;

    companion object {
        fun from(value: String): AppEnvironment =
            entries.firstOrNull { it.name.equals(value, true) }
                ?: DEV
    }
}
