package kz.kff.domain.usecase.auth_session.command

import jakarta.validation.Validator
import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.auth_session.AuthSessionDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.auth_session.AuthSessionCDTO
import kz.kff.domain.dto.auth_session.AuthSessionRDTO
import kz.kff.domain.mapper.toAuthSessionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class CreateAuthSessionUseCase(
    private val authSessionDatasource: AuthSessionDatasource,
    private val validator: Validator,
) : UseCaseTransaction() {

    suspend operator fun invoke(dto: AuthSessionCDTO): AuthSessionRDTO = tx(
        before = { validateDTO(validator, dto) }
    ) {
        authSessionDatasource.create(
            insertBlock = {
                applyMap(dto.bindMap(AuthSessionTable))
            },
            mapper = { it.toAuthSessionRDTO() }
        ) ?: throw ApiInternalException(messageKey = LocalizedMessageConstraints.ErrorInternalMessage)
    }
}
