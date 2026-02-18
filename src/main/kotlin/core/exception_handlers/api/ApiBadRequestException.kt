package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiBadRequestException(
    messageKey: String = LocalizedMessageConstraints.ErrorBadRequestMessage,
    vararg args: Any,
    innerCode:Int? = 400,
): ApiBaseException(statusCode = 400, innerCode = innerCode, LocalizedMessage(messageKey, args))