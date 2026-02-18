package kz.kff.domain.mapper

import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.domain.dto.role_permission.RolePermissionRDTO
import kz.kff.domain.dto.role_permission.RolePermissionSDTO
import kz.kff.domain.dto.role_permission.RolePermissionWithDetailsRDTO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRolePermissionRDTO(): RolePermissionRDTO {
    return RolePermissionRDTO(
        id = this[RolePermissionTable.id].value,
        roleId = this[RolePermissionTable.roleId].value,
        permissionId = this[RolePermissionTable.permissionId].value,
    )
}

fun ResultRow.toRolePermissionSDTO(): RolePermissionSDTO {
    return RolePermissionSDTO(
        id = this[RolePermissionTable.id].value,
        roleId = this[RolePermissionTable.roleId].value,
        permissionId = this[RolePermissionTable.permissionId].value,
    )
}

fun ResultRow.toRolePermissionWithDetailsRDTO(): RolePermissionWithDetailsRDTO {
    return RolePermissionWithDetailsRDTO(
        id = this[RolePermissionTable.id].value,
        role = this.toRoleSDTO(),
        permission = this.toPermissionSDTO(),
    )
}

fun Iterable<ResultRow>.toRolePermissionRDTOList() = map { it.toRolePermissionRDTO() }

fun Iterable<ResultRow>.toRolePermissionSDTOList() = map { it.toRolePermissionSDTO() }

fun Iterable<ResultRow>.toRolePermissionWithDetailsRDTOList() = map { it.toRolePermissionWithDetailsRDTO() }
