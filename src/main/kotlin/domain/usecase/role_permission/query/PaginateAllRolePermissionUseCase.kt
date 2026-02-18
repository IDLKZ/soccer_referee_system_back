package kz.kff.domain.usecase.role_permission.query

import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.role_permission.RolePermissionWithDetailsRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRolePermissionWithDetailsRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.filter.role_permission.RolePermissionFilter

class PaginateAllRolePermissionUseCase(
    private val rolePermissionDatasource: RolePermissionDatasource
) : UseCaseTransaction() {

    suspend operator fun invoke(filter: RolePermissionFilter): PaginationMeta<RolePermissionWithDetailsRDTO> {
        return tx {
            rolePermissionDatasource.paginate(
                filter = filter,
                mapper = eachRow { it.toRolePermissionWithDetailsRDTO() }
            )
        }
    }
}