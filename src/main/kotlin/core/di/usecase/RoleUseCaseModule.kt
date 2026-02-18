package kz.kff.core.di.usecase

import org.koin.core.module.Module
import kz.kff.domain.usecase.role.command.BulkCreateRoleUseCase
import kz.kff.domain.usecase.role.command.BulkDeleteRoleUseCase
import kz.kff.domain.usecase.role.command.BulkRestoreRoleUseCase
import kz.kff.domain.usecase.role.command.BulkUpdateRoleUseCase
import kz.kff.domain.usecase.role.command.CreateRoleUseCase
import kz.kff.domain.usecase.role.command.DeleteRoleByIdCase
import kz.kff.domain.usecase.role.command.RestoreRoleByIdCase
import kz.kff.domain.usecase.role.command.UpdateRoleByIdCase
import kz.kff.domain.usecase.role.query.GetAllRolesUseCase
import kz.kff.domain.usecase.role.query.GetRoleByIdUseCase
import kz.kff.domain.usecase.role.query.PaginateRoleUseCase

fun Module.roleQueryUseCases() {
    factory { PaginateRoleUseCase(get()) }
    factory { GetAllRolesUseCase(get()) }
    factory { GetRoleByIdUseCase(get()) }
}

fun Module.roleCommandUseCases() {
    factory { CreateRoleUseCase(get(), get()) }
    factory { UpdateRoleByIdCase(get(), get()) }
    factory { RestoreRoleByIdCase(get()) }
    factory { DeleteRoleByIdCase(get()) }
}

fun Module.roleBulkUseCases() {
    factory { BulkCreateRoleUseCase(get(), get()) }
    factory { BulkUpdateRoleUseCase(get(), get()) }
    factory { BulkDeleteRoleUseCase(get()) }
    factory { BulkRestoreRoleUseCase(get()) }
}