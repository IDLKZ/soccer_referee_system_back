package kz.kff.core.di

import kz.kff.core.config.AppDatabaseConfig
import kz.kff.core.config.AppFlyWayConfig
import kz.kff.core.db.DatabaseFactory
import org.koin.dsl.module
import org.koin.dsl.onClose

fun initAppDatabaseModule(dbConfig: AppDatabaseConfig, flywayConfig: AppFlyWayConfig) = module {
    single { dbConfig }
    single { flywayConfig }
    single(createdAtStart = true) {
        DatabaseFactory(get()).apply { init() }
    }.onClose { it?.close() }

    single {
        get<DatabaseFactory>().getDatabase()
    }
}