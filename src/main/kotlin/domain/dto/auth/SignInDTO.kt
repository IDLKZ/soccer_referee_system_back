package kz.kff.domain.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable
import kz.kff.core.shared.constraints.DataConstraints
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

@Serializable
data class SignInDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val login: String,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:Size(max = DataConstraints.StandardVarcharLength, message = LocalizedMessageConstraints.ValidationFieldMaxSizeMessage)
    val password: String,
)
