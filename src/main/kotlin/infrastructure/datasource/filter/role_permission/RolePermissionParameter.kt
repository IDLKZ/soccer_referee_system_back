package kz.kff.infrastructure.datasource.filter.role_permission

import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kotlin.reflect.KClass

class RolePermissionParameter(filterClass: KClass<RolePermissionFilter>): BaseQueryParameter<RolePermissionTable, RolePermissionFilter>(filterClass)

