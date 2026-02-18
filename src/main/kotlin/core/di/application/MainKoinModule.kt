package kz.kff.core.di.application

import io.ktor.server.application.Application
import io.ktor.server.application.install
import kz.kff.core.config.AppDatabaseConfig
import kz.kff.core.config.AppFlyWayConfig
import kz.kff.core.config.StorageConfig
import kz.kff.core.di.datasourceModule
import kz.kff.core.di.initAppDatabaseModule
import kz.kff.core.di.service.storageModule
import kz.kff.core.di.useCaseModule
import kz.kff.core.di.validationModule
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.koinModule(dbConfig: AppDatabaseConfig,flyWayConfig: AppFlyWayConfig,storageConfig: StorageConfig) {
    install(Koin) {
        //Логгирование
        slf4jLogger()
        modules(
            //Инициализация к БД (открытие и закрытие)
            initAppDatabaseModule(dbConfig=dbConfig, flywayConfig=flyWayConfig),
            //Datasource DI
            datasourceModule,
            //File Service
            storageModule(storageConfig),
            //Use Case
            useCaseModule,
            //Validation
            validationModule,
        )
    }
}