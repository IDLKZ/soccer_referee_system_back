package kz.kff.core.di

import kz.kff.core.db.table.file.FileTable
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.datasource.db.file.FileDatasource
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.infrastructure.datasource.db.file.FileDatasourceImpl
import kz.kff.infrastructure.datasource.db.permission.PermissionDatasourceImpl
import kz.kff.infrastructure.datasource.db.role.RoleDatasourceImpl
import kz.kff.infrastructure.datasource.db.role_permission.RolePermissionDatasourceImpl
import kz.kff.infrastructure.datasource.db.user.UserDatasourceImpl
import org.koin.dsl.module

val datasourceModule = module {
    single<RoleDatasource> { RoleDatasourceImpl(RoleTable) }
    single<PermissionDatasource> { PermissionDatasourceImpl(PermissionTable) }
    single<RolePermissionDatasource> { RolePermissionDatasourceImpl(RolePermissionTable) }
    single<FileDatasource> { FileDatasourceImpl(FileTable) }
    single<UserDatasource> { UserDatasourceImpl(UserTable) }
}