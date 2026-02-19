package kz.kff.domain.usecase.user.command

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteUserByIdUseCase(
    private val userDatasource: UserDatasource,
) : UseCaseTransaction() {

    suspend operator fun invoke(id: Long, hardDelete: Boolean = false): Boolean = tx {
        userDatasource.findByLongId(id = id, showDeleted = true, mapper = eachRow { it.toUserRDTO() })
            ?: throw ApiNotFoundException(messageKey = LocalizedMessageConstraints.UserNotFoundMessage)
        userDatasource.delete(id = id, hardDelete = hardDelete)
    }
}
