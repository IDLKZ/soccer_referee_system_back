package kz.kff.domain.usecase.auth_session.query

import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.auth_session.AuthSessionRDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.auth_session.AuthSessionFilter

class PaginateAuthSessionUseCase(
    private val authSessionDatasource: AuthSessionDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(filter: AuthSessionFilter): PaginationMeta<AuthSessionRDTO> = tx {
        authSessionDatasource.paginate(filter, eachRow { it.toAuthSessionRDTO() })
    }
}
