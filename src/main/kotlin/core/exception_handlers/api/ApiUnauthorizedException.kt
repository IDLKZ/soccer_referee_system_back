package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiUnauthorizedException (
    messageKey: String = LocalizedMessageConstraints.ErrorUnauthorizedMessage,
    innerCode: Int = 401,
    statusCode: Int = 401,
    detail: String? = null
):ApiBaseException(statusCode, innerCode, LocalizedMessage(messageKey), detail)