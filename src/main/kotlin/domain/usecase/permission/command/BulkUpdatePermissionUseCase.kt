package kz.kff.domain.usecase.permission.command

import jakarta.validation.Validator
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.exception_handlers.api.ApiBadRequestException
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.dto.permission.PermissionUDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter
import org.jetbrains.exposed.v1.core.statements.UpdateStatement

class BulkUpdatePermissionUseCase(
    private val permissionDatasource: PermissionDatasource,
    private val validator: Validator
) : UseCaseTransaction() {

    operator suspend fun invoke(dtos: List<PermissionUDTO>): List<PermissionRDTO> {
        if (dtos.isEmpty()) {
            return emptyList()
        }
        return tx(
            before = {
                dtos.forEach { dto -> validateDTO(validator, dto) }
            }
        ) {
            val values: List<String> = dtos.map { it.data.value }

            val filter = PermissionFilter(
                table = PermissionTable,
                values = values
            )

            val existingPermissions: List<PermissionRDTO> =
                permissionDatasource.findAllWithFilter(
                    filter = filter,
                    mapper = eachRow { it.toPermissionRDTO() }
                )

            val valueToIdMap: Map<String, Long> =
                existingPermissions.associate { it.value to it.id }

            val readyDTOs: List<PermissionUDTO> = dtos.map { dto ->
                val existingIdForValue = valueToIdMap[dto.data.value]
                when {
                    existingIdForValue == null -> dto
                    existingIdForValue == dto.id -> dto
                    else -> throw ApiBadRequestException(
                        LocalizedMessageConstraints.ValidationUniqueValueExistMessage
                    )
                }
            }
            val updates = readyDTOs.map { dto ->
                dto.id to { it: UpdateStatement ->
                    dto.data.applyUpdate(PermissionTable, it)
                }
            }
            permissionDatasource.bulkUpdate(
                updates = updates,
                mapper = { it.toPermissionRDTO() }
            )
        }
    }
}
