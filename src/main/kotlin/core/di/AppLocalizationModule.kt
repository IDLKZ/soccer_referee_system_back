package kz.kff.core.di

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.util.AttributeKey
import kz.kff.core.localization.SupportedLocale

private val LocaleKey = AttributeKey<SupportedLocale>("Locale")

val ApplicationCall.locale: SupportedLocale
    get() = attributes.getOrNull(LocaleKey) ?: SupportedLocale.RU

fun Application.configureLocalization() {
    intercept(ApplicationCallPipeline.Plugins) {
        val acceptLanguage = call.request.headers["Accept-Language"]
        val locale = SupportedLocale.fromHeader(acceptLanguage)
        call.attributes.put(LocaleKey, locale)
    }
}