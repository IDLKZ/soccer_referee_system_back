package kz.kff.core.config

import io.ktor.server.config.ApplicationConfig

/**
 * Конфигурация Flyway для управления миграциями базы данных.
 *
 * Определяет, включены ли миграции, откуда загружаются SQL-скрипты
 * и какие проверки выполняются при запуске приложения.
 *
 * Используется для автоматического применения и валидации
 * схемы базы данных при старте сервиса.
 *
 * @property enabled
 * Включает или отключает Flyway-миграции.
 * Если `false`, миграции не будут выполняться при запуске приложения.
 *
 * @property locations
 * Расположение миграционных скриптов.
 * Поддерживает `classpath:` и файловую систему.
 * По умолчанию: `classpath:db/migration`.
 *
 * @property baselineOnMigrate
 * Разрешает создание baseline при выполнении миграций.
 * Используется при подключении Flyway к уже существующей базе данных,
 * содержащей схему без истории миграций.
 *
 * @property validateOnMigrate
 * Включает проверку применённых миграций перед выполнением новых.
 * При несовпадении схемы и истории миграций будет выброшено исключение.
 */
data class AppFlyWayConfig(
    val enabled: Boolean,
    val locations: String,
    val baselineOnMigrate: Boolean,
    val validateOnMigrate: Boolean
){
    companion object{
        fun configure(config: ApplicationConfig): AppFlyWayConfig{
            val appFlyWayConfig = config.config("flyway")
            return AppFlyWayConfig(
                enabled = appFlyWayConfig.propertyOrNull("enabled")?.getString()?.toBoolean() ?: true,
                locations = appFlyWayConfig.propertyOrNull("locations")?.getString() ?: "classpath:db/migration",
                baselineOnMigrate = appFlyWayConfig.propertyOrNull("baselineOnMigrate")?.getString()?.toBoolean() ?: true,
                validateOnMigrate = appFlyWayConfig.propertyOrNull("validateOnMigrate")?.getString()?.toBoolean() ?: true
            )
        }
    }
}
