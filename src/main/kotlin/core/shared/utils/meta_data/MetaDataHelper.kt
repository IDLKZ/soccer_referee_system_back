package kz.kff.core.shared.utils.meta_data

import io.ktor.server.routing.RoutingRequest
import kz.kff.domain.dto.meta_data.MetaDataDTO
import ua_parser.CachingParser

class MetaDataHelper {

    companion object {

        private val uaParser = CachingParser()

        fun getMetaDataDTO(request: RoutingRequest): MetaDataDTO {
            val userAgent = request.headers["User-Agent"]
            val ipAddress = request.headers["X-Forwarded-For"]
                ?.split(",")
                ?.first()
                ?.trim()
                ?: request.local.remoteAddress

            val parsed = parseDevice(userAgent)

            return MetaDataDTO(
                deviceName = parsed?.device?.family,
                deviceOs = parsed?.os?.family,
                deviceType = resolveType(parsed?.device?.family),
                browser = parsed?.userAgent?.family,
                ipAddress = ipAddress,
                userAgent = userAgent,
            )
        }

        private fun parseDevice(userAgent: String?): ua_parser.Client? {
            if (userAgent == null) return null
            return uaParser.parse(userAgent)
        }

        private fun resolveType(deviceFamily: String?): String? {
            return when {
                deviceFamily == null -> null
                deviceFamily.contains("iPhone", true) -> "mobile"
                deviceFamily.contains("Android", true) -> "mobile"
                deviceFamily.contains("iPad", true) -> "tablet"
                else -> "web"
            }
        }
    }
}
