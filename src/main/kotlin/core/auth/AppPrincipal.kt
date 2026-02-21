package kz.kff.core.auth

import io.ktor.server.auth.Principal

data class AppPrincipal(
    val userId: Long,
    val sessionId: Long,
    val role: String?,
    val permissions: List<String>,
) : Principal
