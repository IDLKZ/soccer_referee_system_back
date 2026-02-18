package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.shared.constraints.LocalizedMessageConstraints

class ApiConflictException(
    messageKey: String = LocalizedMessageConstraints.ErrorConflictMessage,
    vararg args: Any
) : ApiBaseException(409, 409, LocalizedMessage(messageKey, args))