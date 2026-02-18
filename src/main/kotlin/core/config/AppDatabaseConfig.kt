package kz.kff.core.config

import io.ktor.server.config.ApplicationConfig

/**
 * Данный конфигуратор для AppDatabaseConfig позволяет ему работать с конфигурациями класса database в application.yaml
 * Конфигурация пула соединений HikariCP.
 *
 * Используется для управления жизненным циклом JDBC-соединений,
 * ограничением параллельных подключений и таймингами.
 *
 * @property jdbcUrl
 * JDBC URL для подключения к базе данных.
 * Пример: `jdbc:postgresql://localhost:5432/app_db`
 *
 * @property username
 * Имя пользователя базы данных.
 * Определяет уровень доступа приложения к данным.
 *
 * @property password
 * Пароль пользователя базы данных.
 * ⚠️ Не должен логироваться или храниться в открытом виде.
 *
 * @property driverClassName
 * Полное имя JDBC-драйвера.
 * По умолчанию используется `org.postgresql.Driver`.
 *
 * @property maximumPoolSize
 * Максимальное количество JDBC-соединений в пуле.
 * Ограничивает параллелизм доступа к базе данных.
 *
 * @property minimumIdle
 * Минимальное количество простаивающих соединений.
 * Используется для ускорения первых запросов.
 *
 * @property idleTimeout
 * Максимальное время простоя соединения (в миллисекундах),
 * после которого оно будет закрыто.
 *
 * @property connectionTimeout
 * Максимальное время ожидания свободного соединения из пула
 * перед выбросом исключения.
 *
 * @property maxLifetime
 * Максимальное время жизни соединения (в миллисекундах).
 * Используется для предотвращения использования устаревших соединений.
 *
 * @property poolName
 * Человекочитаемое имя пула соединений.
 * Используется в логах, метриках и JMX.
 */
data class AppDatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val driverClassName: String,
    val maximumPoolSize: Int,
    val minimumIdle: Int,
    val idleTimeout: Long,
    val connectionTimeout: Long,
    val maxLifetime: Long,
    val poolName: String
){
    companion object {
        fun configure(config: ApplicationConfig): AppDatabaseConfig {
            //key - value
            val databaseConfig = config.config("database")
            //
            return AppDatabaseConfig(
                jdbcUrl = databaseConfig.property("jdbcUrl").getString(),
                username = databaseConfig.property("username").getString(),
                password = databaseConfig.property("password").getString(),
                driverClassName = databaseConfig.propertyOrNull("driverClassName")?.getString() ?: "org.postgresql.Driver",
                maximumPoolSize = databaseConfig.propertyOrNull("maximumPoolSize")?.getString()?.toInt() ?: 10,
                minimumIdle = databaseConfig.propertyOrNull("minimumIdle")?.getString()?.toInt() ?: 2,
                idleTimeout = databaseConfig.propertyOrNull("idleTimeout")?.getString()?.toLong() ?: 600_000,
                connectionTimeout = databaseConfig.propertyOrNull("connectionTimeout")?.getString()?.toLong() ?: 30_000,
                maxLifetime = databaseConfig.propertyOrNull("maxLifetime")?.getString()?.toLong() ?: 1_800_000,
                poolName = databaseConfig.propertyOrNull("poolName")?.getString() ?: "MainHikariPool"
            )
        }
    }
}
