package kz.kff.infrastructure.datasource.db.auth_session

import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.infrastructure.datasource.db.BaseDbDatasourceImpl

class AuthSessionDatasourceImpl(
    table: AuthSessionTable
) : BaseDbDatasourceImpl<AuthSessionTable>(table), AuthSessionDatasource
