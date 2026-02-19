package kz.kff.domain.usecase.user.query

import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.datasource.db.user.UserDatasource
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.user.UserRDTO
import kz.kff.domain.dto.user.UserWithRelationsDTO
import kz.kff.domain.mapper.toRolePermissionWithDetailsRDTO
import kz.kff.domain.mapper.toUserRDTO
import kz.kff.domain.mapper.toUserWithRelationsDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.user.UserFilter

class PaginateUserUseCase(
    private val userDatasource: UserDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(filter: UserFilter): PaginationMeta<UserWithRelationsDTO> = tx {
        userDatasource.paginate(filter, eachRow { it.toUserWithRelationsDTO() })
    }
}
