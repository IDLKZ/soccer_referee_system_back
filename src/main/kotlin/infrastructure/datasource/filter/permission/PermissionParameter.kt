package kz.kff.infrastructure.datasource.filter.permission

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter
import kotlin.reflect.KClass

class PermissionQueryParameter(filterClass: KClass<PermissionFilter>) : BaseQueryParameter<PermissionTable, PermissionFilter>(filterClass)
