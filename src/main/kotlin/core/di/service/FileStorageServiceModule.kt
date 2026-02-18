package kz.kff.core.di.service

import kz.kff.core.config.StorageConfig
import kz.kff.domain.datasource.service.file.FileService
import kz.kff.infrastructure.datasource.service.file.FileServiceImpl
import org.koin.dsl.module

fun storageModule(storageConfig: StorageConfig) = module {
    single<FileService> { FileServiceImpl(storageConfig) }
}