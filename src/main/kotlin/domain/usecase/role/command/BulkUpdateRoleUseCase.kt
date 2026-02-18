package kz.kff.domain.usecase.role.command

import jakarta.validation.Validator
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.dto.role.RoleUDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

class BulkUpdateRoleUseCase(
    private val roleDatasource: RoleDatasource,
    private val validator: Validator
) : UseCaseTransaction() {

    operator suspend fun invoke(dtos: List<RoleUDTO>):List<RoleRDTO>{
        if(dtos.isEmpty()){
            return emptyList()
        }
        return tx(
            before = {
                dtos.forEach { dto -> validateDTO(validator,dto)}
            }
        ) {
            val values: List<String> = dtos.map { it.data.value }

            val filter = RoleFilter(
                table = RoleTable,
                values = values
            )

            val existingRoles: List<RoleRDTO> =
                roleDatasource.findAllWithFilter(
                    filter = filter,
                    mapper = eachRow { it.toRoleRDTO() }
                )

            val valueToIdMap: Map<String, Long> =
                existingRoles.associate { it.value to it.id }


            val readyDTOs: List<RoleUDTO> = dtos.map { dto ->
                val existingIdForValue = valueToIdMap[dto.data.value]
                when {
                    // value свободен → можно
                    existingIdForValue == null -> dto

                    // value принадлежит этому же id → можно (update)
                    existingIdForValue == dto.id -> dto

                    // value занят другим id → НЕЛЬЗЯ
                    else -> throw ApiBadRequestException(
                        LocalizedMessageConstraints.ValidationUniqueValueExistMessage
                    )
                }
            }
            val updates = readyDTOs.map { dto ->
                dto.id to { it: UpdateStatement ->
                    dto.data.applyUpdate(RoleTable, it)
                }
            }
            roleDatasource.bulkUpdate(
                updates = updates,
                mapper = { it.toRoleRDTO() }
            )
        }

    }
}