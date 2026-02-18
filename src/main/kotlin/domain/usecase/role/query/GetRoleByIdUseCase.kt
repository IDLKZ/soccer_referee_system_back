package kz.kff.domain.usecase.role.query

import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import kz.kff.domain.mapper.toRoleWithPermissionsDTOGrouped
import kz.kff.domain.usecase.shared.UseCaseTransaction

class GetRoleByIdUseCase(
    private val roleDatasource: RoleDatasource
) : UseCaseTransaction() {
    suspend operator fun invoke(id: Long, showDeleted: Boolean? = true, includeJoin: Boolean? = false): RoleWithPermissionsDTO = tx {
        roleDatasource.findByLongId(id, showDeleted = showDeleted, includeJoin = includeJoin) { rows ->
            rows.toRoleWithPermissionsDTOGrouped()
        } ?: throw ApiNotFoundException(LocalizedMessageConstraints.RoleNotFoundMessage)
    }
}
