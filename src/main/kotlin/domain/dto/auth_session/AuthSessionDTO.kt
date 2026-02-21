package kz.kff.domain.dto.auth_session

import jakarta.validation.constraints.NotNull
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.serializers.LocalDateTimeIso8601Serializer
import kotlinx.serialization.Serializable
import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.core.shared.constraints.LocalizedMessageConstraints
import kz.kff.domain.dto.BaseCreateDTO
import kz.kff.domain.dto.BaseOneCreateDTO
import kz.kff.domain.dto.BaseUpdateDTO

@Serializable
data class AuthSessionRDTO(
    val id: Long,
    val userId: Long,
    val deviceName: String? = null,
    val deviceOs: String? = null,
    val deviceType: String? = null,
    val browser: String? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val country: String? = null,
    val city: String? = null,
    val expiresAt: Long,
    val revoked: Boolean,
    val revokedAt: Long? = null,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeIso8601Serializer::class)
    val updatedAt: LocalDateTime,
)

@Serializable
data class AuthSessionCDTO(
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val userId: Long,
    val deviceName: String? = null,
    val deviceOs: String? = null,
    val deviceType: String? = null,
    val browser: String? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val country: String? = null,
    val city: String? = null,
    @field:NotNull(message = LocalizedMessageConstraints.ValidationFieldRequiredMessage)
    val expiresAt: Long,
    val revoked: Boolean = false,
    val revokedAt: Long? = null,
) : BaseCreateDTO<AuthSessionTable>, BaseUpdateDTO<AuthSessionTable>, BaseOneCreateDTO<AuthSessionTable>
