package kz.kff.core.di.service

import kz.kff.core.config.JWTConfig
import kz.kff.domain.datasource.service.jwt.AppJwtService
import kz.kff.infrastructure.datasource.service.jwt.AppJwtServiceImpl
import org.koin.dsl.module

fun authCoreModule(jwtConfig: JWTConfig) = module {
    single<AppJwtService> { AppJwtServiceImpl(jwtConfig) }
}