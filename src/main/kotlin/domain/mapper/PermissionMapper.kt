package kz.kff.domain.mapper

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.domain.dto.permission.PermissionRDTO
import kz.kff.domain.dto.permission.PermissionSDTO
import kz.kff.domain.dto.permission.PermissionWithRelationsDTO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toPermissionRDTO(): PermissionRDTO {
    return PermissionRDTO(
        id = this[PermissionTable.id].value,
        titleRu = this[PermissionTable.titleRu],
        titleKk = this[PermissionTable.titleKk],
        titleEn = this[PermissionTable.titleEn],
        value = this[PermissionTable.value],
        createdAt = this[PermissionTable.createdAt],
        updatedAt = this[PermissionTable.updatedAt],
    )
}

fun ResultRow.toPermissionSDTO(): PermissionSDTO{
    return PermissionSDTO(
        id = this[PermissionTable.id].value,
        titleRu = this[PermissionTable.titleRu],
        titleKk = this[PermissionTable.titleKk],
        titleEn = this[PermissionTable.titleEn],
        value = this[PermissionTable.value],
    )
}

fun Iterable<ResultRow>.toPermissionWithRelationsDTOList(): List<PermissionWithRelationsDTO> {
    return groupBy { it[PermissionTable.id].value }.map { (_, rows) ->
        val first = rows.first()
        PermissionWithRelationsDTO(
            id = first[PermissionTable.id].value,
            titleRu = first[PermissionTable.titleRu],
            titleKk = first[PermissionTable.titleKk],
            titleEn = first[PermissionTable.titleEn],
            value = first[PermissionTable.value],
            roles = rows
                .filter { it.getOrNull(RoleTable.id) != null }
                .map { it.toRoleRDTO() }
                .ifEmpty { null },
            createdAt = first[PermissionTable.createdAt],
            updatedAt = first[PermissionTable.updatedAt],
        )
    }
}

fun Iterable<ResultRow>.toPermissionRDTOList() = map { it.toPermissionRDTO() }

fun Iterable<ResultRow>.toPermissionSDTOList() = map { it.toPermissionSDTO() }