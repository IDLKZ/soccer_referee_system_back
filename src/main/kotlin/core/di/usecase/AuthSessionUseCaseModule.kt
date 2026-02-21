package kz.kff.core.di.usecase

import kz.kff.domain.usecase.auth_session.command.CreateAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.command.DeleteAuthSessionByIdUseCase
import kz.kff.domain.usecase.auth_session.command.UpdateAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.query.GetAuthSessionUseCase
import kz.kff.domain.usecase.auth_session.query.PaginateAuthSessionUseCase
import org.koin.core.module.Module

fun Module.queryAuthSessionUseCases() {
    factory { PaginateAuthSessionUseCase(get()) }
    factory { GetAuthSessionUseCase(get()) }
}

fun Module.commandAuthSessionUseCases() {
    factory { CreateAuthSessionUseCase(get(), get()) }
    factory { UpdateAuthSessionUseCase(get(), get()) }
    factory { DeleteAuthSessionByIdUseCase(get()) }
}
