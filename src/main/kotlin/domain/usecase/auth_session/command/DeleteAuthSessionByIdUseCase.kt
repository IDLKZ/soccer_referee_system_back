package kz.kff.domain.usecase.auth_session.command

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteAuthSessionByIdUseCase(
    private val authSessionDatasource: AuthSessionDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long): Boolean = tx {
        authSessionDatasource.findByLongId(id = id, mapper = eachRow { it.toAuthSessionRDTO() })
            ?: throw ApiNotFoundException(LocalizedMessageConstraints.AuthSessionNotFoundMessage)

        authSessionDatasource.delete(id = id, hardDelete = true)
    }
}
