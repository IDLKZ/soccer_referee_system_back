package kz.kff.domain.mapper

import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.dto.role.RoleRDTO
import kz.kff.domain.dto.role.RoleSDTO
import kz.kff.domain.dto.role.RoleWithPermissionsDTO
import org.jetbrains.exposed.v1.core.ResultRow
import kotlin.collections.ifEmpty
import kotlin.collections.mapNotNull

fun ResultRow.toRoleRDTO(): RoleRDTO{
    return RoleRDTO(
        id = this[RoleTable.id].value,
        titleRu = this[RoleTable.titleRu],
        titleKk = this[RoleTable.titleKk],
        titleEn = this[RoleTable.titleEn],
        value = this[RoleTable.value],
        isSystem = this[RoleTable.isSystem],
        isAdministrative = this[RoleTable.isAdministrative],
        createdAt = this[RoleTable.createdAt],
        updatedAt = this[RoleTable.updatedAt],
        deletedAt = this[RoleTable.deletedAt],
    )
}

fun ResultRow.toRoleWithPermissionsDTO(): RoleWithPermissionsDTO{
    return RoleWithPermissionsDTO(
        id = this[RoleTable.id].value,
        titleRu = this[RoleTable.titleRu],
        titleKk = this[RoleTable.titleKk],
        titleEn = this[RoleTable.titleEn],
        value = this[RoleTable.value],
        isSystem = this[RoleTable.isSystem],
        isAdministrative = this[RoleTable.isAdministrative],
        permissions =this.getOrNull(PermissionTable.id)?.let {
            listOf(this.toPermissionRDTO())
        } ?: emptyList(),
        createdAt = this[RoleTable.createdAt],
        updatedAt = this[RoleTable.updatedAt],
        deletedAt = this[RoleTable.deletedAt],
    )
}

fun ResultRow.toRoleSDTO(): RoleSDTO{
    return RoleSDTO(
        id = this[RoleTable.id].value,
        titleRu = this[RoleTable.titleRu],
        titleKk = this[RoleTable.titleKk],
        titleEn = this[RoleTable.titleEn],
        value = this[RoleTable.value],
    )
}

fun Iterable<ResultRow>.toRoleWithPermissionsDTOList() = map { it.toRoleWithPermissionsDTO() }

fun Iterable<ResultRow>.toRoleWithPermissionsDTOGrouped(): List<RoleWithPermissionsDTO> {
    return groupBy { it[RoleTable.id].value }.map { (_, rows) ->
        val first = rows.first()
        RoleWithPermissionsDTO(
            id = first[RoleTable.id].value,
            titleRu = first[RoleTable.titleRu],
            titleKk = first[RoleTable.titleKk],
            titleEn = first[RoleTable.titleEn],
            value = first[RoleTable.value],
            isSystem = first[RoleTable.isSystem],
            isAdministrative = first[RoleTable.isAdministrative],
            permissions = rows
                .filter { it.getOrNull(PermissionTable.id) != null }
                .map { it.toPermissionRDTO() },
            createdAt = first[RoleTable.createdAt],
            updatedAt = first[RoleTable.updatedAt],
            deletedAt = first[RoleTable.deletedAt],
        )
    }
}

fun Iterable<ResultRow>.toRoleRDTOList() = map { it.toRoleRDTO() }

fun Iterable<ResultRow>.toRoleSDTOList() = map { it.toRoleSDTO() }