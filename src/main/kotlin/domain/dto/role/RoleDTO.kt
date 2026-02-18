package kz.kff.domain.dto.role

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable
import kz.kff.core.db.table.role.RoleTable
import kz.kff.core.shared.constraints.DataConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO
import kz.kff.domain.dto.permission.PermissionRDTO

@Serializable
data class RoleRDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String,
    val isSystem: Boolean,
    val isAdministrative: Boolean,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val deletedAt: LocalDateTime? = null,
)

@Serializable
data class RoleWithPermissionsDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String,
    val isSystem: Boolean,
    val isAdministrative: Boolean,
    val permissions : List<PermissionRDTO>? = null,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val deletedAt: LocalDateTime? = null,
)

@Serializable
data class RoleSDTO(
    val id: Long,
    val titleRu: String,
    val titleKk: String? = null,
    val titleEn: String? = null,
    val value: String
)

@Serializable
data class RoleUDTO(
    val id: Long,
    val data: RoleCDTO
)


@Serializable
data class RoleCDTO(
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleRu: String,
    @field:Size(max = DataConstraints.StandardVarcharLength,message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleKk: String? = null,
    @field:Size(max = DataConstraints.StandardVarcharLength,message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val titleEn: String? = null,
    @field:Size(max = DataConstraints.StandardUniqueValueLength,message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val value: String,
    val isSystem: Boolean? = false,
    val isAdministrative: Boolean? = false,
): BaseCreateDTO<RoleTable>,BaseUpdateDTO<RoleTable>,BaseOneCreateDTO<RoleTable>