package kz.kff.core.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kz.kff.core.config.AppDatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.slf4j.LoggerFactory
import javax.sql.DataSource

/**
 * Фабрика для инициализации и управления подключением к базе данных.
 *
 * Отвечает за:
 * - создание и конфигурацию пула соединений HikariCP
 * - подключение Exposed [Database] к источнику данных
 * - корректное завершение работы пула соединений
 *
 * Используется на инфраструктурном уровне приложения.
 * Не должен применяться в domain или application слоях напрямую.
 *
 * @property config конфигурация базы данных и пула соединений
 */
class DatabaseFactory(private val config: AppDatabaseConfig) {

    /** Источник данных HikariCP */
    private lateinit var dataSource: HikariDataSource

    /** Экземпляр Exposed Database */
    private lateinit var database: Database

    /** Логгер для инфраструктурных событий базы данных */
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    /**
     * Инициализирует пул соединений и подключает Exposed к базе данных.
     *
     * Создаёт [HikariDataSource] на основе конфигурации,
     * затем выполняет подключение через [Database.connect].
     *
     * Должен вызываться один раз при старте приложения.
     *
     * @return инициализированный экземпляр [Database]
     */
    fun init(): Database {
        dataSource = createDataSource()
        database = Database.connect(dataSource)
        logger.info("✅ Database connection pool initialized: ${config.poolName}")
        return database
    }

    /**
     * Создаёт и конфигурирует [HikariDataSource].
     *
     * Применяет все параметры пула соединений:
     * URL, учётные данные, тайм-ауты и ограничения.
     *
     * @return настроенный экземпляр [HikariDataSource]
     */
    private fun createDataSource(): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.username
            password = config.password
            driverClassName = config.driverClassName
            maximumPoolSize = config.maximumPoolSize
            minimumIdle = config.minimumIdle
            idleTimeout = config.idleTimeout
            connectionTimeout = config.connectionTimeout
            maxLifetime = config.maxLifetime
            poolName = config.poolName
            connectionTestQuery = "SELECT 1"
            isAutoCommit = true
        }
        return HikariDataSource(hikariConfig)
    }

    /**
     * Возвращает инициализированный экземпляр [Database].
     *
     * @throws UninitializedPropertyAccessException
     * если метод [init] не был вызван ранее
     */
    fun getDatabase(): Database = database

    /**
     * Возвращает используемый [DataSource].
     *
     * Может применяться для:
     * - health-check
     * - мониторинга
     * - интеграции с другими библиотеками
     *
     * @throws UninitializedPropertyAccessException
     * если метод [init] не был вызван ранее
     */
    fun getDataSource(): DataSource = dataSource

    /**
     * Корректно закрывает пул соединений базы данных.
     *
     * Безопасно завершает работу [HikariDataSource],
     * освобождая все ресурсы.
     *
     * Рекомендуется вызывать при остановке приложения.
     */
    fun close() {
        if (::dataSource.isInitialized && !dataSource.isClosed) {
            dataSource.close()
            logger.info("🔒 Database connection pool closed")
        }
    }
}
