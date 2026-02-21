package kz.kff.core.di.usecase

import kz.kff.domain.usecase.auth.command.RefreshTokenUseCase
import kz.kff.domain.usecase.auth.command.RegisterUseCase
import kz.kff.domain.usecase.auth.command.SignInUseCase
import org.koin.core.module.Module

fun Module.commandAuthUseCases() {
    factory { SignInUseCase(get(), get(), get(), get()) }
    factory { RegisterUseCase(get(), get(), get(), get(), get()) }
    factory { RefreshTokenUseCase(get(), get(), get(), get()) }
}
