package kz.kff.infrastructure.datasource.filter.user

import kz.kff.core.db.table.user.UserTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kotlin.reflect.KClass

class UserParameter(filterClass: KClass<UserFilter>) : BaseQueryParameter<UserTable, UserFilter>(filterClass)
