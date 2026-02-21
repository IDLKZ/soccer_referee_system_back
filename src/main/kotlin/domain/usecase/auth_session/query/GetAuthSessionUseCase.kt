package kz.kff.domain.usecase.auth_session.query

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.dto.auth_session.AuthSessionRDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class GetAuthSessionUseCase(
    private val authSessionDatasource: AuthSessionDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long): AuthSessionRDTO = tx {
        authSessionDatasource.findByLongId(id = id) { rows ->
            rows.map { it.toAuthSessionRDTO() }
        } ?: throw ApiNotFoundException(LocalizedMessageConstraints.AuthSessionNotFoundMessage)
    }
}
