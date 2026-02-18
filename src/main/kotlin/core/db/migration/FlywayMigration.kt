package kz.kff.core.db.migration

import kz.kff.core.config.AppDatabaseConfig
import kz.kff.core.config.AppFlyWayConfig
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory

/**
 * Утилита для выполнения миграций базы данных с использованием Flyway.
 *
 * Отвечает за:
 * - проверку включённости Flyway-миграций
 * - конфигурацию Flyway на основе настроек приложения
 * - выполнение миграций схемы базы данных
 * - логирование текущего состояния и результатов миграций
 *
 * Используется на инфраструктурном уровне приложения
 * и вызывается, как правило, при старте сервиса.
 */
object FlywayMigration {

    /** Логгер для событий, связанных с миграциями базы данных */
    private val logger = LoggerFactory.getLogger(FlywayMigration::class.java)

    /**
     * Запускает процесс миграции базы данных.
     *
     * Если миграции отключены в конфигурации ([AppFlyWayConfig.enabled] == false),
     * выполнение будет пропущено.
     *
     * При включённых миграциях:
     * - выводится текущее состояние миграций
     * - выполняется применение новых миграций
     * - логируется итоговый статус схемы базы данных
     *
     * В случае ошибки выполнение прерывается с пробросом исключения.
     *
     * @param dbConfig конфигурация подключения к базе данных
     * @param flywayConfig конфигурация Flyway-миграций
     *
     * @throws Exception если выполнение миграций завершилось ошибкой
     */
    fun run(dbConfig: AppDatabaseConfig, flywayConfig: AppFlyWayConfig) {
        if (!flywayConfig.enabled) {
            logger.info("⏭️ Flyway migrations disabled")
            return
        }

        logger.info("🚀 Running Flyway migrations...")

        val flyway = Flyway.configure()
            .dataSource(dbConfig.jdbcUrl, dbConfig.username, dbConfig.password)
            .locations(flywayConfig.locations)
            .baselineOnMigrate(flywayConfig.baselineOnMigrate)
            .validateOnMigrate(flywayConfig.validateOnMigrate)
            .load()

        try {
            logMigrationStatus(flyway)

            val result = flyway.migrate()

            if (result.migrationsExecuted > 0) {
                logger.info("✅ Migrations applied: ${result.migrationsExecuted}")
                logger.info("📦 Target schema version: ${result.targetSchemaVersion}")
            } else {
                logger.info("✅ Database is up to date")
            }
        } catch (e: Exception) {
            logger.error("❌ Migration failed: ${e.message}", e)
            throw e
        }
    }

    /**
     * Логирует текущее состояние всех известных миграций Flyway.
     *
     * Выводит информацию о версии, описании и статусе
     * каждой миграции (applied, pending, failed и т.д.).
     *
     * Используется для диагностики и прозрачности
     * процесса миграции базы данных.
     *
     * @param flyway инициализированный экземпляр [Flyway]
     */
    private fun logMigrationStatus(flyway: Flyway) {
        val info = flyway.info()

        if (info.all().isNotEmpty()) {
            logger.info("📊 Migration status:")
            info.all().forEach { migration ->
                logger.info(
                    "  - ${migration.version}: ${migration.description} [${migration.state}]"
                )
            }
        }
    }
}
