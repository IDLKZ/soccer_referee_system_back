package kz.kff.core.di

import kz.kff.core.di.usecase.commandAuthSessionUseCases
import kz.kff.core.di.usecase.commandAuthUseCases
import kz.kff.core.di.usecase.commandFileUseCase
import kz.kff.core.di.usecase.commandRolePermissionUseCase
import kz.kff.core.di.usecase.commandUserUseCases
import kz.kff.core.di.usecase.permissionBulkUseCases
import kz.kff.core.di.usecase.permissionCommandUseCases
import kz.kff.core.di.usecase.permissionQueryUseCases
import kz.kff.core.di.usecase.queryAuthSessionUseCases
import kz.kff.core.di.usecase.queryFileUseCase
import kz.kff.core.di.usecase.queryRolePermissionUseCase
import kz.kff.core.di.usecase.queryUserUseCases
import kz.kff.core.di.usecase.roleBulkUseCases
import kz.kff.core.di.usecase.roleCommandUseCases
import kz.kff.core.di.usecase.roleQueryUseCases
import org.koin.dsl.module

val useCaseModule = module {
    // Role Use Cases
    roleQueryUseCases()
    roleCommandUseCases()
    roleBulkUseCases()

    // Permission Use Cases
    permissionQueryUseCases()
    permissionCommandUseCases()
    permissionBulkUseCases()

    //Role Permission
    queryRolePermissionUseCase()
    commandRolePermissionUseCase()

    //File
    queryFileUseCase()
    commandFileUseCase()

    //User
    queryUserUseCases()
    commandUserUseCases()

    //Auth Session
    queryAuthSessionUseCases()
    commandAuthSessionUseCases()

    //Auth
    commandAuthUseCases()
}