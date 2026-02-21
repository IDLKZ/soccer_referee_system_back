package kz.kff.core.di
import kz.kff.infrastructure.datasource.seeder.permission.PermissionSeeder
import kz.kff.infrastructure.datasource.seeder.role.RoleSeeder
import kz.kff.infrastructure.datasource.seeder.role_permission.AdminRolePermissionSeeder
import kz.kff.infrastructure.datasource.seeder.user.UserSeeder
import org.koin.dsl.module

val seederModule = module {
    factory { RoleSeeder(get()) }
    factory { PermissionSeeder(get()) }
    factory { AdminRolePermissionSeeder(get(),get(),get()) }
    factory { UserSeeder(get(),get(),get()) }
}