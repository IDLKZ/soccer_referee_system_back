package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiValidationException (
    messageKey: String = LocalizedMessageConstraints.ErrorValidationdMessage,
    errors: Map<String, LocalizedMessage>,
    statusCode: Int = 422,
    innerCode: Int = 422,
): ApiBaseException(statusCode, innerCode, LocalizedMessage(messageKey), validationError = errors)