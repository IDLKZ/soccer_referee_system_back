package kz.kff.core.exception_handlers.api

import kz.kff.core.localization.LocalizedMessage
import kz.kff.core.localization.SupportedLocale

open class ApiBaseException(
    val statusCode: Int,
    val innerCode: Int?,
    val localizedMessage: LocalizedMessage,
    val detail: String? = null,
    val validationError: Map<String, LocalizedMessage>? = null
) : RuntimeException(localizedMessage.key){

    fun getResolvedMessage(locale: SupportedLocale): String {
        return localizedMessage.resolve(locale)
    }

    fun getResolvedValidationErrors(locale: SupportedLocale): Map<String, String>? {
        return validationError?.mapValues { it.value.resolve(locale) }
    }
}