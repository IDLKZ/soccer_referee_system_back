package kz.kff.domain.usecase.role_permission.command

import io.ktor.server.html.insert
import kz.kff.domain.datasource.db.eachRow
import jakarta.validation.Validator
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.datasource.db.role_permission.RolePermissionDatasource
import kz.kff.domain.dto.role_permission.RolePermissionCDTO
import kz.kff.domain.dto.role_permission.RolePermissionRDTO
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.mapper.toRolePermissionRDTO
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction

class CreateRolePermissionUseCase(
    private val rolePermissionDatasource: RolePermissionDatasource,
    private val roleDatasource: RoleDatasource,
    private val permissionDatasource: PermissionDatasource,
    private val validator: Validator,
) : UseCaseTransaction() {
    suspend operator fun invoke(dto: RolePermissionCDTO): RolePermissionRDTO {
        return tx(
            before = {
                validateDTO(validator = validator, dto = dto)
            }
        ) {
            this.checkRoleAndPermission(dto)
            rolePermissionDatasource.create(
                insertBlock = dto.createEntity(RolePermissionTable),
                mapper = { it -> it.toRolePermissionRDTO() }
            ) ?: throw ApiInternalException()
        }
    }


    suspend private fun checkRoleAndPermission(dto: RolePermissionCDTO): Unit {
        val existingRole = roleDatasource.findByLongId(
            id = dto.roleId,
            showDeleted = null,
            mapper = eachRow { it.toRoleRDTO() }
        )

        if (existingRole == null) {
            throw ApiBadRequestException(
                messageKey = LocalizedMessageConstraints.RoleNotFoundMessage
            )
        }

        val existingPermission = permissionDatasource.findByLongId(
            id = dto.permissionId,
            showDeleted = null,
            mapper = eachRow { it.toPermissionRDTO() }
        )

        if (existingPermission == null) {
            throw ApiBadRequestException(
                messageKey = LocalizedMessageConstraints.PermissionNotFoundMessage
            )
        }

    }
}