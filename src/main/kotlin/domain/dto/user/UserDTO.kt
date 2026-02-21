package kz.kff.domain.dto.user
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable
import kz.kff.core.db.table.user.UserTable
import kz.kff.core.shared.constraints.DataConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.core.shared.constraints.ValidationConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO
import kz.kff.domain.dto.file.FileRDTO
import kz.kff.domain.dto.role.RoleSDTO
import kz.kff.domain.dto.role.RoleWithPermissionsDTO

@Serializable
data class UserRDTO(
    val id: Long,
    val roleId: Long? = null,
    val imageId: Long? = null,
    val username: String,
    val phone: String? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String? = null,
    @Serializable(with = LocalDateIso8601Serializer::class)
    val birthDate: LocalDate? = null,
    val gender: Int? = null,
    val isActive: Boolean,
    val isVerified: Boolean,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val deletedAt: LocalDateTime? = null,
)

@Serializable
data class UserWithRelationsDTO(
    val id: Long,
    val username: String,
    val phone: String? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String? = null,
    @Serializable(with = LocalDateIso8601Serializer::class)
    val birthDate: LocalDate? = null,
    val gender: Int? = null,
    val isActive: Boolean,
    val isVerified: Boolean,
    val role: RoleWithPermissionsDTO? = null,
    val image: FileRDTO? = null,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val deletedAt: LocalDateTime? = null,
)

@Serializable
data class UserSecretRDTO(
    val id: Long,
    val username: String,
    val phone: String? = null,
    val email: String,
    val firstName: String,
    val lastName: String,
    val patronymic: String? = null,
    val passwordHash: String? = null,
    @Serializable(with = LocalDateIso8601Serializer::class)
    val birthDate: LocalDate? = null,
    val gender: Int? = null,
    val isActive: Boolean,
    val isVerified: Boolean,
    val role: RoleWithPermissionsDTO? = null,
    val image: FileRDTO? = null,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val deletedAt: LocalDateTime? = null,
) {
    fun toUserRDTO() = UserRDTO(
        id = id,
        roleId = role?.id,
        imageId = image?.id,
        username = username,
        phone = phone,
        email = email,
        firstName = firstName,
        lastName = lastName,
        patronymic = patronymic,
        birthDate = birthDate,
        gender = gender,
        isActive = isActive,
        isVerified = isVerified,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )

    fun toUserWithRelationsDTO() = UserWithRelationsDTO(
        id = id,
        username = username,
        phone = phone,
        email = email,
        firstName = firstName,
        lastName = lastName,
        patronymic = patronymic,
        birthDate = birthDate,
        gender = gender,
        isActive = isActive,
        isVerified = isVerified,
        role = role,
        image = image,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )
}

@Serializable
data class UserSDTO(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
)

@Serializable
data class UserUDTO(
    val id: Long,
    val data: UserCDTO,
)

@Serializable
data class UserCDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val roleId: Long,
    var imageId: Long? = null,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardUniqueValueLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    @field:Pattern(regexp = ValidationConstraints.LOGIN_REGEX, message = LocalizedMessageConstraints.ValidationLoginPatternMessage)
    val username: String,
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val phone: String? = null,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardUniqueValueLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    @field:Email(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val email: String,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val firstName: String,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val lastName: String,
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val patronymic: String? = null,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Pattern(regexp = ValidationConstraints.PASSWORD_REGEX, message = LocalizedMessageConstraints.ValidationPasswordPatternMessage)
    var passwordHash: String,
    @Serializable(with = LocalDateIso8601Serializer::class)
    val birthDate: LocalDate? = null,
    val gender: Int? = null,
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
) : BaseCreateDTO<UserTable>, BaseUpdateDTO<UserTable>, BaseOneCreateDTO<UserTable>
