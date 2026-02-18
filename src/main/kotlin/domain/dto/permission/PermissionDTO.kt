package kz.kff.domain.dto.permission

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kz.kff.core.db.table.permission.PermissionTable
import kz.kff.core.shared.constraints.DataConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO
import kz.kff.domain.dto.role.RoleRDTO

@Serializable
data class PermissionRDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@Serializable
data class PermissionWithRelationsDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String,
    val roles: List<RoleRDTO>? = null,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

@Serializable
data class PermissionSDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String
)

@Serializable
data class PermissionUDTO(
    val id: Long,
    val data: PermissionCDTO
)

@Serializable
data class PermissionCDTO(
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleRu: String,
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleKk: String? = null,
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleEn: String? = null,
    @field:Size(max = DataConstraints.StandardUniqueValueLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val value: String,
) : BaseCreateDTO<PermissionTable>, BaseUpdateDTO<PermissionTable>, BaseOneCreateDTO<PermissionTable>
