package kz.kff.domain.dto.role_permission

import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable
import kz.kff.core.db.table.role_permission.RolePermissionTable
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO
import kz.kff.domain.dto.permission.PermissionSDTO
import kz.kff.domain.dto.role.RoleSDTO

@Serializable
data class RolePermissionRDTO(
    val id: Long,
    val roleId: Long,
    val permissionId: Long,
)

@Serializable
data class RolePermissionWithDetailsRDTO(
    val id: Long,
    val role: RoleSDTO,
    val permission: PermissionSDTO,
)

@Serializable
data class RolePermissionSDTO(
    val id: Long,
    val roleId: Long,
    val permissionId: Long,
)

@Serializable
data class RolePermissionUDTO(
    val id: Long,
    val data: RolePermissionCDTO
)

@Serializable
data class RolePermissionCDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val roleId: Long,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val permissionId: Long,
) : BaseCreateDTO<RolePermissionTable>, BaseUpdateDTO<RolePermissionTable>, BaseOneCreateDTO<RolePermissionTable>
