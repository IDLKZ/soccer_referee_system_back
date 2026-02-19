package kz.kff.core.di.usecase

import kz.kff.domain.usecase.file.command.BulkCreateFileUseCase
import kz.kff.domain.usecase.file.command.CreateFileUseCase
import kz.kff.domain.usecase.file.command.DeleteBulkFileUseCase
import kz.kff.domain.usecase.file.command.DeleteFileUseCase
import kz.kff.domain.usecase.file.command.UpdateFileUseCase
import kz.kff.domain.usecase.file.query.GetFileByIdUseCase
import kz.kff.domain.usecase.file.query.PaginateFileUseCase
import org.koin.core.module.Module


fun Module.queryFileUseCase() {
    factory { PaginateFileUseCase(get()) }
    factory { GetFileByIdUseCase(get()) }
}

fun Module.commandFileUseCase() {
    factory { CreateFileUseCase(get(), get()) }
    factory { BulkCreateFileUseCase(get(), get()) }
    factory { UpdateFileUseCase(get(), get()) }
    factory { DeleteFileUseCase(get(), get()) }
    factory { DeleteBulkFileUseCase(get(), get()) }
}
