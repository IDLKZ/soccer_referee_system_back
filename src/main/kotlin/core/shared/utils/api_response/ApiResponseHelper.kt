package kz.kff.core.shared.utils.api_response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kz.kff.core.exception_handlers.api.ApiBaseException
import kz.kff.core.localization.SupportedLocale

object ApiResponseHelper {
    suspend inline fun <reified T> ApplicationCall.success(
        data: T,
        message: String? = "OK",
        code: Int? = 200
    ) {
        respond(HttpStatusCode.OK, ApiCommonResponse(message, data, code, null))
    }

    suspend inline fun <reified T> ApplicationCall.created(
        data: T,
        message: String? = "Created"
    ) {
        respond(HttpStatusCode.Created, ApiCommonResponse(message, data, 201, null))
    }

    suspend fun ApplicationCall.noContent() {
        respond(HttpStatusCode.NoContent)
    }

    suspend fun ApplicationCall.deleted(message: String? = "Deleted") {
        respond(HttpStatusCode.OK, ApiCommonResponse(message, EmptyData(), 200, null))
    }

    fun ApiBaseException.toErrorResponse(locale: SupportedLocale, includeTrace: Boolean = false): ApiCommonResponse<EmptyData> {
        return ApiCommonResponse(
            message = null,
            data = EmptyData(),
            code = statusCode,
            error = ApiResponseError(
                innerCode = innerCode,
                message = getResolvedMessage(locale),
                detail = detail,
                errorTrace = if (includeTrace) stackTraceToString() else null,
                validationError = getResolvedValidationErrors(locale)
            )
        )
    }
}