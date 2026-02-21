package kz.kff.infrastructure.datasource.seeder

import kz.kff.core.config.AppEnvironment
import kz.kff.infrastructure.datasource.seeder.permission.PermissionSeeder
import kz.kff.infrastructure.datasource.seeder.role.RoleSeeder
import kz.kff.infrastructure.datasource.seeder.role_permission.AdminRolePermissionSeeder
import kz.kff.infrastructure.datasource.seeder.user.UserSeeder
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SeederRunner : KoinComponent {
    val roleSeeder by inject<RoleSeeder>()
    val permissionSeeder by inject<PermissionSeeder>()
    val adminRolePermissionSeeder by inject<AdminRolePermissionSeeder>()
    val userSeeder by inject<UserSeeder>()

    suspend fun run(env: AppEnvironment) {
        roleSeeder.seed(env)
        permissionSeeder.seed(env)
        adminRolePermissionSeeder.seed(env)
        userSeeder.seed(env)
    }

}