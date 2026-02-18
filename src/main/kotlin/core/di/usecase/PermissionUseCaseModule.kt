package kz.kff.core.di.usecase

import org.koin.core.module.Module
import kz.kff.domain.usecase.permission.command.BulkCreatePermissionUseCase
import kz.kff.domain.usecase.permission.command.BulkDeletePermissionUseCase
import kz.kff.domain.usecase.permission.command.BulkUpdatePermissionUseCase
import kz.kff.domain.usecase.permission.command.CreatePermissionUseCase
import kz.kff.domain.usecase.permission.command.DeletePermissionByIdUseCase
import kz.kff.domain.usecase.permission.command.UpdatePermissionByIdUseCase
import kz.kff.domain.usecase.permission.query.GetAllPermissionsUseCase
import kz.kff.domain.usecase.permission.query.GetPermissionByIdUseCase
import kz.kff.domain.usecase.permission.query.GetPermissionByValueUseCase
import kz.kff.domain.usecase.permission.query.PaginatePermissionUseCase

fun Module.permissionQueryUseCases() {
    factory { PaginatePermissionUseCase(get()) }
    factory { GetAllPermissionsUseCase(get()) }
    factory { GetPermissionByIdUseCase(get()) }
    factory { GetPermissionByValueUseCase(get()) }
}

fun Module.permissionCommandUseCases() {
    factory { CreatePermissionUseCase(get(), get()) }
    factory { UpdatePermissionByIdUseCase(get(), get()) }
    factory { DeletePermissionByIdUseCase(get()) }
}

fun Module.permissionBulkUseCases() {
    factory { BulkCreatePermissionUseCase(get(), get()) }
    factory { BulkUpdatePermissionUseCase(get(), get()) }
    factory { BulkDeletePermissionUseCase(get()) }
}
