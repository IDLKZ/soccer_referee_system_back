package kz.kff.domain.usecase.role.query

import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.PaginationMeta
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import kz.kff.domain.mapper.toRoleWithPermissionsDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter

class PaginateRoleUseCase(
    private val roleDatasource: RoleDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(filter: RoleFilter): PaginationMeta<RoleWithPermissionsDTO> = tx {
        roleDatasource.paginate(filter) { rows ->
            rows.toRoleWithPermissionsDTOGrouped()
        }
    }
}
