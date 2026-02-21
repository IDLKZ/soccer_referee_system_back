package kz.kff.core.di.usecase

import kz.kff.domain.usecase.user.command.BulkCreateUserUseCase
import kz.kff.domain.usecase.user.command.BulkDeleteUserByIdsUseCase
import kz.kff.domain.usecase.user.command.CreateUserUseCase
import kz.kff.domain.usecase.user.command.DeleteUserByIdUseCase
import kz.kff.domain.usecase.user.command.UpdateUserUseCase
import kz.kff.domain.usecase.user.query.GetUserByIdUseCase
import kz.kff.domain.usecase.user.query.PaginateUserUseCase
import org.koin.core.module.Module

fun Module.queryUserUseCases() {
    factory { PaginateUserUseCase(get()) }
    factory { GetUserByIdUseCase(get()) }
}

fun Module.commandUserUseCases() {
    factory { CreateUserUseCase(get(), get(), get(), get(), get(),get()) }
    factory { UpdateUserUseCase(get(), get(), get(),get()) }
    factory { DeleteUserByIdUseCase(get()) }
    factory { BulkDeleteUserByIdsUseCase(get()) }
    factory { BulkCreateUserUseCase(get(), get(), get(),get()) }
}
