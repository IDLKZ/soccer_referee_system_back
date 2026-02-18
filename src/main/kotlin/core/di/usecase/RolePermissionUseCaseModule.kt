package kz.kff.core.di.usecase

import kz.kff.domain.usecase.role_permission.command.CreateRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.command.DeleteRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.query.AllRolePermissionUseCase
import kz.kff.domain.usecase.role_permission.query.PaginateAllRolePermissionUseCase
import org.koin.core.module.Module

fun Module.queryRolePermissionUseCase() {
    factory {
        AllRolePermissionUseCase(get())
    }
    factory {
        PaginateAllRolePermissionUseCase(get())
    }
}

fun Module.commandRolePermissionUseCase() {
    factory {
        CreateRolePermissionUseCase(
            rolePermissionDatasource = get(),
            roleDatasource = get(),
            permissionDatasource = get(),
            validator = get()
        )
    }
    factory {
        DeleteRolePermissionUseCase(
            rolePermissionDatasource = get(),
        )
    }
}
