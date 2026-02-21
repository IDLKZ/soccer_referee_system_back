package kz.kff.domain.dto.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import kotlinx.serialization.Serializable
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

@Serializable
data class RefreshDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    @field:NotBlank(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val refreshToken: String,
)
