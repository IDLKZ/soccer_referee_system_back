package kz.kff.domain.usecase.permission.command

import jakarta.validation.Validator
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.permission.PermissionCDTO
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter

class UpdatePermissionByIdUseCase(
    private val permissionDatasource: PermissionDatasource,
    private val validator: Validator
) : UseCaseTransaction() {
    operator suspend fun invoke(id: Long, dto: PermissionCDTO): PermissionRDTO = tx(
        before = {
            validateDTO(validator, dto)
        }
    ) {
        val existing = permissionDatasource.findByLongId(id, mapper = eachRow { it.toPermissionRDTO() })

        if (existing == null) {
            throw ApiNotFoundException()
        }
        val existingValuePermission = permissionDatasource.findOneByFilter(
            filter = PermissionFilter.byValue(dto.value),
            mapper = { it.toPermissionRDTO() }
        )

        if (existingValuePermission != null && existingValuePermission.id != existing.id) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ValidationUniqueValueExistMessage, dto.value)
        }
        permissionDatasource.update(
            id = id,
            updateBlock = { dto.applyUpdate(PermissionTable, this) },
            mapper = { it.toPermissionRDTO() }
        ) ?: throw ApiInternalException()
    }
}
