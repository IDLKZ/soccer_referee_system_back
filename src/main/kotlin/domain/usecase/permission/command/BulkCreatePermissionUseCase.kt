package kz.kff.domain.usecase.permission.command

import jakarta.validation.Validator
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.domain.datasource.db.permission.PermissionDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.permission.PermissionCDTO
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toPermissionRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.permission.PermissionFilter

class BulkCreatePermissionUseCase(
    private val permissionDatasource: PermissionDatasource,
    private val validator: Validator
) : UseCaseTransaction() {

    operator suspend fun invoke(dtos: List<PermissionCDTO>): List<PermissionRDTO> {
        if (dtos.isEmpty()) return emptyList()
        return tx(
            before = {
                dtos.forEach { validateDTO(validator, it) }
            }
        )
        {
            val values: List<String> = dtos.map { it.value }

            val filter = PermissionFilter(
                table = PermissionTable,
                values = values
            )

            val existingPermissions: List<PermissionRDTO> =
                permissionDatasource.findAllWithFilter(
                    filter = filter,
                    mapper = eachRow { it.toPermissionRDTO() }
                )

            val existingValues: Set<String> =
                existingPermissions.map { it.value }.toSet()

            val dtosToCreate: List<PermissionCDTO> =
                dtos.filterNot { it.value in existingValues }

            permissionDatasource.bulkCreate(
                items = dtosToCreate,
                insertBlock = { dto -> applyMap(dto.bindMap(PermissionTable)) },
                mapper = { it.toPermissionRDTO() }
            )
        }
    }
}
