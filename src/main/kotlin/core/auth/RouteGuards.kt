package kz.kff.core.auth

import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.routing.intercept
import kz.kff.core.exception_handlers.api.ApiForbiddenException
import kz.kff.core.exception_handlers.api.ApiUnauthorizedException

/**
 * Требует валидный JWT ACCESS токен.
 */
fun Route.authenticated(build: Route.() -> Unit) {
    authenticate(JWT_ACCESS_AUTH) {
        build()
    }
}

/**
 * Требует валидный JWT и наличие хотя бы одной из указанных ролей.
 */
fun Route.requireRole(vararg roles: String, build: Route.() -> Unit) {
    authenticate(JWT_ACCESS_AUTH) {
        intercept(ApplicationCallPipeline.Call) {
            val principal = call.principal<AppPrincipal>()
                ?: throw ApiUnauthorizedException()
            if (principal.role !in roles) {
                throw ApiForbiddenException()
            }
        }
        build()
    }
}

/**
 * Требует валидный JWT и наличие хотя бы одного из указанных permissions.
 */
fun Route.requirePermission(vararg permissions: String, build: Route.() -> Unit) {
    authenticate(JWT_ACCESS_AUTH) {
        intercept(ApplicationCallPipeline.Call) {
            val principal = call.principal<AppPrincipal>()
                ?: throw ApiUnauthorizedException()
            if (permissions.none { it in principal.permissions }) {
                throw ApiForbiddenException()
            }
        }
        build()
    }
}

/**
 * Достать AppPrincipal из текущего call (внутри authenticated/requireRole/requirePermission).
 */
fun io.ktor.server.application.ApplicationCall.appPrincipal(): AppPrincipal =
    principal<AppPrincipal>() ?: throw ApiUnauthorizedException()
