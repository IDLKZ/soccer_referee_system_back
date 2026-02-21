package kz.kff.domain.mapper

import kz.kff.core.db.table.auth_session.AuthSessionTable
import kz.kff.domain.dto.auth_session.AuthSessionRDTO
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toAuthSessionRDTO(): AuthSessionRDTO {
    return AuthSessionRDTO(
        id = this[AuthSessionTable.id].value,
        userId = this[AuthSessionTable.userId].value,
        deviceName = this[AuthSessionTable.deviceName],
        deviceOs = this[AuthSessionTable.deviceOs],
        deviceType = this[AuthSessionTable.deviceType],
        browser = this[AuthSessionTable.browser],
        ipAddress = this[AuthSessionTable.ipAddress],
        userAgent = this[AuthSessionTable.userAgent],
        country = this[AuthSessionTable.country],
        city = this[AuthSessionTable.city],
        expiresAt = this[AuthSessionTable.expiresAt],
        revoked = this[AuthSessionTable.revoked],
        revokedAt = this[AuthSessionTable.revokedAt],
        createdAt = this[AuthSessionTable.createdAt],
        updatedAt = this[AuthSessionTable.updatedAt],
    )
}

fun Iterable<ResultRow>.toAuthSessionRDTOList() = map { it.toAuthSessionRDTO() }
