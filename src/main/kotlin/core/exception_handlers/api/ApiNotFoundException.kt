package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiNotFoundException (
    messageKey: String = LocalizedMessageConstraints.ErrorNotFoundMessage,
    innerCode: Int = 404,
    errorCode: Int = 404,
    detail: String? = null
): ApiBaseException(404, innerCode, LocalizedMessage(messageKey), detail)