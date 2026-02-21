package kz.kff.domain.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class TokenDTO(
    val access_token: String,
    val refresh_token: String?,
    val expires_in_ms: Long,
)
