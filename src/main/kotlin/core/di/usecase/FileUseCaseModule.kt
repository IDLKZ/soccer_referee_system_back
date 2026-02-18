package kz.kff.core.di.usecase

import kz.kff.domain.usecase.file.command.CreateFileUseCase
import kz.kff.domain.usecase.file.query.PaginateFileUseCase
import org.koin.core.module.Module
import org.koin.dsl.module


fun Module.queryFileUseCase() {
    factory {
        PaginateFileUseCase(
            get()
        )
    }
}

fun Module.commandFileUseCase() {
    factory {
        CreateFileUseCase(
            get(),
            get()
        )
    }
}