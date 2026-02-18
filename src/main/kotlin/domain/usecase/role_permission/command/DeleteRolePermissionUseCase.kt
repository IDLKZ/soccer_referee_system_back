package kz.kff.domain.usecase.role_permission.command

import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRolePermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class DeleteRolePermissionUseCase (
    private val rolePermissionDatasource: RolePermissionDatasource
): UseCaseTransaction() {

    suspend operator fun invoke(id:Long): Boolean{
        return tx{
            val existing = rolePermissionDatasource.findByLongId(
                id=id,
                showDeleted = true,
                mapper = eachRow { it.toRolePermissionRDTO() }
            )
            if(existing == null){
                throw ApiBadRequestException(
                    messageKey = LocalizedMessageConstraints.RolePermissionNotFoundMessage
                )
            }
            rolePermissionDatasource.delete(id=id, hardDelete = true)
        }
    }
}