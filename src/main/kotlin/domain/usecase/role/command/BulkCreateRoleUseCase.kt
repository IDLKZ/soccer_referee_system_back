package kz.kff.domain.usecase.role.command

import jakarta.validation.Validator
import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.datasource.db.role.RoleDatasource
import kz.kff.domain.dto.applyMap
import kz.kff.domain.dto.role.RoleCDTO
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.datasource.db.eachRow
import kz.kff.domain.mapper.toRoleRDTO
import kz.kff.domain.usecase.shared.UseCaseTransaction
import kz.kff.infrastructure.datasource.db.filter.role.RoleFilter

class BulkCreateRoleUseCase(
    private val roleDatasource: RoleDatasource,
    private val validator: Validator
) : UseCaseTransaction() {

    operator suspend fun invoke(dtos:List<RoleCDTO>):List<RoleRDTO>{
        if (dtos.isEmpty()) return emptyList()
       return tx(
            before = {
                dtos.forEach { validateDTO(validator, it) }
            }
        )
        {
            //Check if some roles already exists and ignore it
            val values: List<String> = dtos.map { it.value }

            val filter = RoleFilter(
                table = RoleTable,
                values = values
            )

            val existingRoles: List<RoleRDTO> =
                roleDatasource.findAllWithFilter(
                    filter = filter,
                    mapper = eachRow { it.toRoleRDTO() }
                )

            val existingValues: Set<String> =
                existingRoles.map { it.value }.toSet()

            val dtosToCreate: List<RoleCDTO> =
                dtos.filterNot { it.value in existingValues }

            roleDatasource.bulkCreate(
                items = dtosToCreate,
                insertBlock = { dto -> applyMap(dto.bindMap(RoleTable)) },
                mapper = {it.toRoleRDTO()}
            )
        }
    }

}