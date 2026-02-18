package kz.kff.infrastructure.datasource.filter.role

import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import kotlin.reflect.KClass

class RoleQueryParameter(filterClass: KClass<RoleFilter>) : BaseQueryParameter<RoleTable, RoleFilter>(filterClass) {}