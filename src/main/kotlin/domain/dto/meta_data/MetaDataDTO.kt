package kz.kff.domain.dto.meta_data

import kotlinx.serialization.Serializable
import kz.kff.domain.dto.auth_session.AuthSessionCDTO

@Serializable
data class MetaDataDTO(
    val deviceName: String? = null,
    val deviceOs: String? = null,
    val deviceType: String? = null,
    val browser: String? = null,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val country: String? = null,
    val city: String? = null
) {
    fun toAuthSessionCDTO(userId: Long, expiresAt: Long) = AuthSessionCDTO(
        userId = userId,
        deviceName = deviceName,
        deviceOs = deviceOs,
        deviceType = deviceType,
        browser = browser,
        ipAddress = ipAddress,
        userAgent = userAgent,
        country = country,
        city = city,
        expiresAt = expiresAt,
    )
}