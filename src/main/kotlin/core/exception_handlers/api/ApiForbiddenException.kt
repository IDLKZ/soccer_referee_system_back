package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiForbiddenException (
    messageKey: String = LocalizedMessageConstraints.ErrorForbiddenMessage,
    vararg args: Any,
    innerCode: Int = 403,
    errorCode: Int = 403,
    detail: String? = null
) : ApiBaseException(403, innerCode, LocalizedMessage(messageKey, args), detail)