package kz.kff.domain.usecase.role.query

import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import kz.kff.domain.mapper.toRoleWithPermissionsDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter

class GetAllRolesUseCase(
    private val roleDatasource: RoleDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(filter: RoleFilter): List<RoleWithPermissionsDTO> = tx {
        roleDatasource.findAllWithFilter(filter) { rows ->
            rows.toRoleWithPermissionsDTOGrouped()
        }
    }
}
