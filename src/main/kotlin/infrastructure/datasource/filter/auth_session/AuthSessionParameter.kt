package kz.kff.infrastructure.datasource.filter.auth_session

import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.domain.datasource.db.filter.BaseQueryParameter
import kotlin.reflect.KClass

class AuthSessionParameter(filterClass: KClass<AuthSessionFilter>) : BaseQueryParameter<AuthSessionTable, AuthSessionFilter>(filterClass)
