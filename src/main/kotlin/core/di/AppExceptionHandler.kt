package kz.kff.core.di

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kz.kff.core.config.AppEnvironmentConfig
import kz.kff.core.exception_handlers.api.ApiBaseException
import kz.kff.core.shared.utils.api_response.ApiCommonResponse
import kz.kff.core.shared.utils.api_response.ApiResponseError
import kz.kff.core.shared.utils.api_response.EmptyData
import kz.kff.core.shared.utils.api_response.isShowError

fun Application.configureApiExceptionHandler(envConfig: AppEnvironmentConfig) {
    install(StatusPages) {
        exception<ApiBaseException> { call, cause ->
            val locale = call.locale
            call.respond(
                HttpStatusCode.fromValue(cause.statusCode),
                ApiCommonResponse(
                    message = null,
                    data = EmptyData(),
                    code = cause.statusCode,
                    error = ApiResponseError(
                        innerCode = cause.innerCode,
                        message = cause.getResolvedMessage(locale),
                        detail = cause.detail,
                        validationError = cause.getResolvedValidationErrors(locale),
                        errorTrace = if (isShowError(envConfig.environment)) cause.stackTraceToString() else null,
                    )
                )
            )
        }
        exception<Exception> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiCommonResponse(
                    message = null,
                    data = EmptyData(),
                    code = 500,
                    error = ApiResponseError(
                        innerCode = 500,
                        message = cause.message,
                        detail = cause.message,
                        validationError = null,
                        errorTrace = if (isShowError(envConfig.environment)) cause.stackTraceToString() else null,
                    )
                )
            )
        }
    }
}