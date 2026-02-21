package kz.kff.domain.dto.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import kotlinx.datetime.LocalDate
import kotlinx.datetime.serializers.LocalDateIso8601Serializer
import kotlinx.serialization.Serializable
import kz.kff.core.shared.constraints.DataConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.core.shared.constraints.ValidationConstraints

@Serializable
data class RegisterDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardUniqueValueLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    @field:Pattern(regexp = ValidationConstraints.LOGIN_REGEX, message = LocalizedMessageConstraints.ValidationLoginPatternMessage)
    val username: String,

    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardUniqueValueLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    @field:Email(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val email: String,

    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Pattern(regexp = ValidationConstraints.PASSWORD_REGEX, message = LocalizedMessageConstraints.ValidationPasswordPatternMessage)
    val password: String,

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

    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val phone: String? = null,

    @Serializable(with = LocalDateIso8601Serializer::class)
    val birthDate: LocalDate? = null,

    val gender: Int? = null,
)
