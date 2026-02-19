package kz.kff.domain.usecase.user.query

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.dto.user.UserWithRelationsDTO
import kz.kff.domain.mapper.toUserWithRelationsDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class GetUserByIdUseCase(
    private val userDatasource: UserDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(
        id: Long,
        showDeleted: Boolean? = null,
        includeJoin: Boolean = true,
    ): UserWithRelationsDTO = tx {
        userDatasource.findByLongId(
            id = id,
            showDeleted = showDeleted,
            includeJoin = includeJoin,
        ) { rows -> rows.map { it.toUserWithRelationsDTO() } }
            ?: throw ApiNotFoundException(LocalizedMessageConstraints.UserNotFoundMessage)
    }
}
