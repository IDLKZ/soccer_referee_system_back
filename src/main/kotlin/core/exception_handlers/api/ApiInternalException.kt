package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiInternalException (
    messageKey: String = LocalizedMessageConstraints.ErrorInternalMessage,
    innerCode: Int = 500,
    errorCode: Int = 500,
    detail: String? = null
) :  ApiBaseException(500, innerCode, LocalizedMessage(messageKey), detail)