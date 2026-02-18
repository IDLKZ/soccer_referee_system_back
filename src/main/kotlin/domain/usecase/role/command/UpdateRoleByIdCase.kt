package kz.kff.domain.usecase.role.command

import jakarta.validation.Validator
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.exception_handlers.api.ApiInternalException
import kz.kff.core.exception_handlers.api.ApiNotFoundException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.role.RoleCDTO
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter

class UpdateRoleByIdCase(
    private val roleDatasource: RoleDatasource,
    private val validator: Validator
) : UseCaseTransaction() {
    operator suspend fun invoke(id:Long,dto: RoleCDTO): RoleRDTO = tx(
        before = {
            validateDTO(validator, dto)
        }
    ) {
        val existing = roleDatasource.findByLongId(id, mapper = eachRow { it.toRoleRDTO() })

        if (existing == null) {
            throw ApiNotFoundException()
        }
        val existingValueRole = roleDatasource.findOneByFilter(
            filter = RoleFilter.byValue(dto.value),
            mapper = { it.toRoleRDTO() }
        )

        if (existingValueRole != null && existingValueRole.id != existing.id) {
            throw ApiBadRequestException(LocalizedMessageConstraints.ValidationUniqueValueExistMessage, dto.value)
        }
        roleDatasource.update(
            id = id,
            updateBlock = { dto.applyUpdate(RoleTable, this) },
            mapper = { it.toRoleRDTO() }
        ) ?: throw ApiInternalException()
    }
}