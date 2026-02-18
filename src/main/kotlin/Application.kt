package kz.kff

import io.ktor.server.application.*
import kz.kff.core.config.AppDatabaseConfig
import kz.kff.core.config.AppEnvironmentConfig
import kz.kff.core.config.AppFlyWayConfig
import kz.kff.core.db.migration.FlywayMigration
import kz.kff.core.di.configureApiExceptionHandler
import kz.kff.core.di.configureLocalization
import kz.kff.core.di.configureSerialization
import kz.kff.core.di.datasourceModule
import kz.kff.core.di.initAppDatabaseModule
import kz.kff.core.di.useCaseModule
import kz.kff.core.di.validationModule
import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.config.SchemaGenerator
import io.github.smiley4.ktoropenapi.config.SchemaOverwriteModule
import io.swagger.v3.oas.models.media.Schema
import kz.kff.core.config.StorageConfig
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kz.kff.core.config.SwaggerConfig
import kz.kff.core.di.application.configureSwaggerLocal
import kz.kff.core.di.application.koinModule

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //Определяем конфигурации
    val envConfig = AppEnvironmentConfig.configure(environment.config)
    val dbConfig = AppDatabaseConfig.configure(environment.config)
    val flywayConfig = AppFlyWayConfig.configure(environment.config)
    val swaggerConfig = SwaggerConfig.configure(environment.config)
    val storageConfig = StorageConfig.configure(environment.config)
    //OpenAPI & Swagger
    if (swaggerConfig.enabled) {
       configureSwaggerLocal(swaggerConfig)
    }
    //Json Serialization Settings для вывода JSON
    configureSerialization()
    //Установка языка
    configureLocalization()
    //Установка ошибок
    configureApiExceptionHandler(envConfig)
    //Единождый запуск миграций
    FlywayMigration.run(dbConfig=dbConfig, flywayConfig=flywayConfig)
    //Koin DI - Repository, UseCase, DB Transactions
    koinModule(dbConfig,flywayConfig,storageConfig)
    //Роутизация
    configureRouting(envConfig,swaggerConfig,storageConfig)
    monitor.subscribe(ApplicationStopped) {
        // Действия при закрытии
    }
}
